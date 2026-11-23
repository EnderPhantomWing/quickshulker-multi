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

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
//#if MC >= 1.21.10
import net.minecraft.client.input.MouseButtonEvent;
//#endif

// A button that cycles an RGB color through the default value and the 16 dye colors.
public class ColorItem extends ConfigItem<Integer> {
    // vanilla dye colors (DyeColor's color getters differ between versions)
    private static final String[] NAMES = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
    private static final int[] COLORS = {0xF9FFFE, 0xF9801D, 0xC74EBD, 0x3AB3DA, 0xFED83D, 0x80C71F, 0xF38BAA, 0x474F52,
            0x9D9D97, 0x169C9C, 0x8932B8, 0x3C44AA, 0x835432, 0x5E7C16, 0xB02E26, 0x1D1D21};

    private final NotSuckyButton colorWidget;

    public ColorItem(Component name, Integer value, Integer defaultValue) {
        super(name, value, defaultValue);
        this.colorWidget = new NotSuckyButton(0, 0, 100, 20, Component.literal("ColorButton"), widget -> setValue(next()));
        setValue(value);
        useDefaultResetBTN();
    }

    // default -> each dye in order -> default; a custom color from the config file goes to the first dye
    private int next() {
        if (this.value.equals(this.defaultValue)) return COLORS[0];
        for (int i = 0; i < COLORS.length - 1; i++) {
            if (COLORS[i] == this.value) return COLORS[i + 1];
        }
        return COLORS[COLORS.length - 1] == this.value ? this.defaultValue : COLORS[0];
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value);
        Component label = Component.translatable("key.kyrptconfig.config.color.custom");
        if (value.equals(this.defaultValue)) {
            label = Component.translatable("key.kyrptconfig.config.color.default");
        } else {
            for (int i = 0; i < COLORS.length; i++) {
                if (COLORS[i] == value) label = Component.translatable("color.minecraft." + NAMES[i]);
            }
        }
        colorWidget.setMessage(label);
        colorWidget.setButtonColor(0xFF000000 | value);
    }

    @Override
    //#if MC >= 1.21.10
    public void mouseClicked(MouseButtonEvent click, boolean doubled) {
        super.mouseClicked(click, doubled);
        colorWidget.mouseClicked(click, doubled);
    }
    //#else
    //$$ public void mouseClicked(double mouseX, double mouseY, int button) {
    //$$     super.mouseClicked(mouseX, mouseY, button);
    //$$     colorWidget.mouseClicked(mouseX, mouseY, button);
    //$$ }
    //#endif

    @Override
    public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(context, x, y, mouseX, mouseY, delta);
        this.colorWidget.setY(y);
        this.colorWidget.setX(resetButton.getX() - resetButton.getWidth() - (colorWidget.getWidth() / 2) - 20);
        //#if MC >= 26.4
        //$$ colorWidget.extractRenderState(context);
        //#else
        colorWidget.render(context, mouseX, mouseY, delta);
        //#endif
    }
}
