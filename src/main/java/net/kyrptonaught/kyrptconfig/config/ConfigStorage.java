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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigStorage {
    private final Path saveFile;
    private final AbstractConfigFile defaultConfig;
    private final JsonLoader jsonLoader;
    private static final Logger logger = Logger.getLogger(ConfigStorage.class.getName());
    public AbstractConfigFile config;

    public ConfigStorage(Path fileName, AbstractConfigFile defaultConfig, JsonLoader jsonLoader) {
        this.saveFile = fileName;
        this.defaultConfig = defaultConfig;
        this.jsonLoader = jsonLoader;
    }

    public void save(String MOD_ID) {
        try (OutputStream os = Files.newOutputStream(saveFile); OutputStreamWriter out = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            String json = jsonLoader.toString(config);
            out.write(json);
        } catch (Exception e) {
            System.out.println(getConfigName(MOD_ID, "Failed to save #CONFIG"));
            logger.log(Level.SEVERE, "Failed to save config", e);
        }
    }

    public AbstractConfigFile load(String MOD_ID) {
        if (!Files.exists(saveFile) || !Files.isReadable(saveFile)) {
            System.out.println(getConfigName(MOD_ID, "Unable to find #CONFIG! Creating a default config"));
            config = defaultConfig;
            return config;
        }

        boolean failed = false;
        try (InputStream in = Files.newInputStream(saveFile, StandardOpenOption.READ)) {
            config = jsonLoader.loadFromInputStream(in, defaultConfig.getClass());
        } catch (Exception e) {
            failed = true;
            logger.log(Level.SEVERE, "Failed to load config", e);
        }
        if (failed || (config == null)) {
            System.out.println(getConfigName(MOD_ID, "Failed to load #CONFIG! Overwriting with default config"));
            config = defaultConfig;
        }
        return config;
    }

    public AbstractConfigFile getDefaultConfig() {
        return defaultConfig;
    }

    private String getConfigName(String MOD_ID, String message) {
        return "[" + MOD_ID + "]: " + message.replaceAll("#CONFIG", "config: " + saveFile.getFileName().toString());
    }
}