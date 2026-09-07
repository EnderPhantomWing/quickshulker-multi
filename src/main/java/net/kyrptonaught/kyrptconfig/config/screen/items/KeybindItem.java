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

package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.api.ConflictHandler;
import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.kyrptonaught.quickshulker.event.KeyBindingRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.language.I18n;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
//#if MC >= 26.2
//$$ import net.minecraft.locale.Language;
//#endif
//#if MC >= 1.21.10
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#endif

//#if MC >= 26.3
//$$ import org.lwjgl.sdl.SDLScancode;
//#else
import org.lwjgl.glfw.GLFW;
//#endif

public class KeybindItem extends ConfigItem<String> {
    private final NotSuckyButton keyButton;
    private Boolean isListening = false;
    private boolean duplicate = false;

    public KeybindItem(Component name, String key, String defaultKey) {
        super(name, key, defaultKey);
        this.keyButton = new NotSuckyButton(0, 0, 100, 20, getCleanName(key), widget -> {
            this.isListening = !this.isListening;
            updateMessage();
        });
        useDefaultResetBTN();
        ConflictHandler.updateMap(this);
    }

    public void setValue(String value) {
        super.setValue(value);
        isListening = false;
        ConflictHandler.updateCustomConflicts();
    }

    public MutableComponent getCleanName(String str) {
        //#if MC >= 26.2
        //$$ if (Language.getInstance().has(value))
        //#else
        if (I18n.exists(value))
        //#endif
            return Component.translatable(str);
        if (str == null || str.isBlank())
            return Component.translatable("key.keyboard.unknown");
        return Component.literal(str.substring(str.length() - 1).toUpperCase());
    }

    public void updateMessage() {
        if (!isListening) {
            duplicate = false;
            MutableComponent mutableText = Component.empty();
            if (isInvalidKeyValue()) {
                for (KeyMapping keyBinding : Minecraft.getInstance().options.keyMappings) {
                    if (KeyBindingRegister.MAIN.equals(keyBinding.getCategory())) {
                        continue;
                    }
                    Component t1 = Component.translatable(keyBinding.getName());
                    Component t2 = this.getTitleText();
                    if (!t1.equals(t2) && keyBinding.saveString().equals(this.value)) {
                        duplicate = true;
                        mutableText.append("\n  - ").append(Component.translatable(keyBinding.getName()));
                    }
                }
                for (KeybindItem item : ConflictHandler.CUSTOM_KEYBIND_ITEMS) {
                    if (item != this && this.value.equals(item.value)) {
                        duplicate = true;
                        mutableText.append("\n  - ").append(item.getTitleText());
                    }
                }
            }
            if (duplicate) {
                //#if MC >= 1.21.10
                keyButton.setMessage(Component.literal("[ ").append(getCleanName(this.value).withStyle(ChatFormatting.WHITE)).append(Component.literal(" ]")).withStyle(ChatFormatting.YELLOW));
                //#else
                //$$ keyButton.setMessage(Component.literal("[ ").append(getCleanName(this.value).withStyle(ChatFormatting.WHITE)).append(Component.literal(" ]")).withStyle(ChatFormatting.RED));
                //#endif
                keyButton.setTooltip(Tooltip.create(Component.translatable("key.kyrptconfig.config.savedValue", Component.literal(this.value)).append(Component.translatable("key.kyrptconfig.config.keybindinsConflict", mutableText))));
            } else {
                keyButton.setMessage(this.getCleanName(this.value));
                keyButton.setTooltip(Tooltip.create(Component.translatable("key.kyrptconfig.config.savedValue", Component.literal(this.value))));
            }
        } else {
            keyButton.setMessage(Component.literal("> ").append(getCleanName(this.value)).append(Component.literal(" <")));
        }
    }

    private boolean isInvalidKeyValue() {
        return this.value != null && !this.value.isEmpty() && !this.value.isBlank() && !this.value.equals("key.keyboard.unknown");
    }

    @Override
    //#if MC >= 1.21.10
    public boolean keyPressed(KeyEvent input) {
        if (isListening) {
            //#if MC >= 26.3
            //$$ if (input.input() == SDLScancode.SDL_SCANCODE_ESCAPE) {
            //#else
            if (input.input() == GLFW.GLFW_KEY_ESCAPE) {
            //#endif
                setValue("");
                return true;
            }
            setValue(InputConstants.getKey(input).getName());
            return true;
        }
        return false;
    }
    //
    @Override
    public void mouseClicked(MouseButtonEvent click, boolean doubled) {
        super.mouseClicked(click, doubled);
        boolean handled;
        handled = (keyButton.mouseClicked(click, doubled) || resetButton.mouseClicked(click, doubled));
        if (isListening && !handled) {
            setValue(InputConstants.Type.MOUSE.getOrCreate(click.button()).getName());
        }
    }
    //#else
    //$$ public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //$$     if (isListening) {
    //$$         if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
    //$$             setValue("");
    //$$             return true;
    //$$         }
    //$$         setValue(InputConstants.getKey(keyCode, scanCode).getName());
    //$$         return true;
    //$$     }
    //$$     return false;
    //$$ }
    //$$
    //$$ @Override
    //$$ public void mouseClicked(double mouseX, double mouseY, int button) {
    //$$     super.mouseClicked(mouseX, mouseY, button);
    //$$     boolean handled;
    //$$     handled = (keyButton.mouseClicked(mouseX, mouseY, button) || resetButton.mouseClicked(mouseX, mouseY, button));
    //$$     if (isListening && !handled) {
    //$$         setValue(InputConstants.Type.MOUSE.getOrCreate(button).getName());
    //$$     }
    //$$ }
    //#endif

    @Override
    public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(context, x, y, mouseX, mouseY, delta);
        this.keyButton.setY(y);

        this.keyButton.setX(resetButton.getX() - resetButton.getWidth() - (keyButton.getWidth() / 2) - 20);

        keyButton.render(context, mouseX, mouseY, delta);

        if (duplicate) {
            int m = keyButton.getX() - 6;
            context.fill(m, y, m + 3, keyButton.getBottom(), -65536);
        }
    }
}