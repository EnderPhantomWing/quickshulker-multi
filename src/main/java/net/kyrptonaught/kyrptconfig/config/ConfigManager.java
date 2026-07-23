/*
 * MIT License
 *
 * Copyright (c) 2019 kyrptonaught
 * Copyright (c) 2024 Haocen2004
 * Copyright (c) 2025 MoRanpcy
 * Copyright (c) 2025 EnderPhantomWing
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.kyrptonaught.kyrptconfig.config;

import net.fabricmc.loader.api.FabricLoader;
import lib.blue.endless.jankson.Jankson;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ConfigManager {
    protected final HashMap<String, ConfigStorage> configs = new HashMap<>();
    protected JsonLoader JANKSON;
    protected Path dir;
    protected String MOD_ID;

    private ConfigManager(String mod_id) {
        this.MOD_ID = mod_id;
        dir = FabricLoader.getInstance().getConfigDir();
        buildJankson();
    }

    public void buildJankson() {
        JANKSON = new JanksonJsonLoader();
        Jankson.Builder builder = CustomJankson.customJanksonBuilder();
        setJANKSON(builder
                //#if MC <= 1.20.6
                //$$ .registerSerializer(ResourceLocation.class, (identifier, marshaller) -> marshaller.serialize(identifier.toString()))
                //$$ .registerDeserializer(String.class, ResourceLocation.class, (s, m) -> ResourceLocation.tryParse(s))
                //#elseif MC >= 1.21.11
                .registerSerializer(Identifier.class, (identifier, marshaller) -> marshaller.serialize(identifier.toString()))
                .registerDeserializer(String.class, Identifier.class, (s, m) -> Identifier.parse(s))
                //#else
                //$$ .registerSerializer(ResourceLocation.class, (identifier, marshaller) -> marshaller.serialize(identifier.toString()))
                //$$ .registerDeserializer(String.class, ResourceLocation.class, (s, m) -> ResourceLocation.parse(s))
                //#endif
                .build());
    }

    public AbstractConfigFile getConfig(String name) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        return configs.get(name).config;
    }

    public AbstractConfigFile getConfigDefault(String name) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        return configs.get(name).getDefaultConfig();
    }

    public void registerFile(String name, AbstractConfigFile defaultConfig) {
        if (JANKSON == null) buildJankson();
        registerFile(name, defaultConfig, JANKSON);
    }

    public void registerFile(String name, AbstractConfigFile defaultConfig, JsonLoader jsonLoader) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        configs.put(name, new ConfigStorage(dir.resolve(name), defaultConfig, jsonLoader));
    }

    public void save() {
        configs.values().forEach(configStorage -> configStorage.save(MOD_ID));
    }

    public void load() {
        configs.values().forEach(configStorage -> configStorage.load(MOD_ID));
        save();
    }

    public Jankson getJANKSON() {
        return ((JanksonJsonLoader) JANKSON).getJankson();
    }

    public void setJANKSON(Jankson jankson) {
        ((JanksonJsonLoader) JANKSON).provideJankson(jankson);
    }

    public static class SingleConfigManager extends ConfigManager {
        public SingleConfigManager(String mod_id, AbstractConfigFile defaultConfig) {
            super(mod_id);
            registerFile(mod_id + "config", defaultConfig);
        }

        public AbstractConfigFile getConfig() {
            return getConfig(MOD_ID + "config");
        }

        public AbstractConfigFile getConfigDefault() {
            return getConfigDefault(MOD_ID + "config");
        }
    }

    public static class MultiConfigManager extends ConfigManager {
        public MultiConfigManager(String mod_id) {
            super(mod_id);
            dir = Path.of(dir + "/" + MOD_ID);
            if (!Files.exists(dir)) {
                try {
                    Files.createDirectories(dir);
                } catch (IOException ignored) {
                }
            }
        }

        public void load(String config) {
            if (!config.endsWith(".json5")) config = config + ".json5";
            this.configs.get(config).load(MOD_ID);
            save(config);
        }

        public void save(String config) {
            if (!config.endsWith(".json5")) config = config + ".json5";
            this.configs.get(config).save(MOD_ID);
        }
    }
}