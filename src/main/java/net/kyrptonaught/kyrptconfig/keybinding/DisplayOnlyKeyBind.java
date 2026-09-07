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

package net.kyrptonaught.kyrptconfig.keybinding;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DisplayOnlyKeyBind extends KeyMapping {
    private final Consumer<InputConstants.Key> keySet;
    private CustomKeyBinding customKeyBinding;

    //#if MC >= 1.21.10
    public DisplayOnlyKeyBind(String translationKey, InputConstants.Type type, int code, KeyMapping.Category category) {
    //#else
    //$$ public DisplayOnlyKeyBind(String translationKey, InputConstants.Type type, int code, String category) {
    //#endif
        super(translationKey, type, code, category);
        keySet = (boundKey) -> {
        };
    }

    //#if MC >= 1.21.10
    public DisplayOnlyKeyBind(String translationKey, KeyMapping.Category category, CustomKeyBinding customKeyBinding, Consumer<InputConstants.Key> keySet) {
    //#else
    //$$ public DisplayOnlyKeyBind(String translationKey, String category, CustomKeyBinding customKeyBinding, Consumer<InputConstants.Key> keySet) {
    //#endif
        super(translationKey, customKeyBinding.getDefaultKey().getType(), customKeyBinding.getDefaultKey().getValue(), category);
        this.customKeyBinding = customKeyBinding;
        this.customKeyBinding.setConsumer(key -> {
            if(!this.key.equals(customKeyBinding.getKeybinding().orElse(null)))
                this.updateSetKey();
        });
        this.keySet = keySet;
        updateSetKey();
    }

    public void setKey(InputConstants.@NotNull Key boundKey) {
        super.setKey(boundKey);
        if (customKeyBinding != null)
            customKeyBinding.setRaw(saveString());
        keySet.accept(boundKey);
    }

    public void updateSetKey() {
        super.setKey(customKeyBinding.getKeybinding().orElse(InputConstants.UNKNOWN));
    }

    @Override
    //#if MC >= 1.21.10
    public KeyMapping.@NotNull Category getCategory() {
    //#else
    //$$ public @NotNull String getCategory() {
    //#endif
        return super.getCategory();
    }

    @Override
    public @NotNull String getName() {
        return super.getName();
    }

    @Override
    public InputConstants.@NotNull Key getDefaultKey() {
        return super.getDefaultKey();
    }
}
