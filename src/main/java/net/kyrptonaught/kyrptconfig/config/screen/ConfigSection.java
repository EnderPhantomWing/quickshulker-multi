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

package net.kyrptonaught.kyrptconfig.config.screen;

import net.kyrptonaught.kyrptconfig.config.screen.items.ConfigItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
//#if MC >= 1.21.10
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#else
//#endif

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigSection extends Screen {

    public List<ConfigItem<?>> configs = new CopyOnWriteArrayList<>();
    public NotSuckyButton sectionSelectionBTN;
    Component title;
    int selectionIndex = 0;
    int scrollOffset = 0;

    public ConfigSection(ConfigScreen configScreen, Component title) {
        super(title);
        this.title = title;
        this.sectionSelectionBTN = new NotSuckyButton(0, 32, 10, 20, title, widget -> configScreen.setSelectedSection(selectionIndex));
        configScreen.addConfigSection(this);
    }

    public void save() {
        for (ConfigItem<?> configItem : configs) {
            configItem.save();
        }
    }

    public int getTotalSectionSize() {
        int size = configs.size() * 3 + 5;
        for (ConfigItem<?> configItem : configs) {
            size += configItem.getSize();
        }
        return size;
    }

    public ConfigItem<?> addConfigItem(ConfigItem<?> item) {
        this.configs.add(item);
        return item;
    }

    public ConfigItem<?> insertConfigItem(ConfigItem<?> item, int slot) {
        this.configs.add(slot, item);
        return item;
    }

    public ConfigItem<?> removeConfigItem(int slot) {
        return this.configs.remove(slot);
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigItem<?> configItem : configs) {
            configItem.tick();
        }
    }

    @Override
    //#if MC >= 1.21.10
    public boolean keyPressed(@NotNull KeyEvent input) {
        for (ConfigItem<?> configItem : configs) {
            if (configItem.keyPressed(input))
                return true;
        }
        return false;
    }
    //
    @Override
    public boolean charTyped(@NotNull CharacterEvent input) {
        for (ConfigItem<?> configItem : configs) {
            if (configItem.charTyped(input))
                return true;
        }
        return false;
    }
    //
    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent click, boolean doubled) {
        for (ConfigItem<?> configItem : configs) {
            configItem.mouseClicked(click, doubled);
        }
        mouseScrolled(click.x(), click.y(), 0,0); // update scroll if option changes screen size
        return false;
    }
    //#else
    //$$ public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //$$     for (ConfigItem<?> configItem : configs) {
    //$$         if (configItem.keyPressed(keyCode, scanCode, modifiers))
    //$$             return true;
    //$$     }
    //$$     return false;
    //$$ }
    //$$
    //$$ @Override
    //$$ public boolean charTyped(char chr, int modifiers) {
    //$$     for (ConfigItem<?> configItem : configs) {
    //$$         if (configItem.charTyped(chr, modifiers))
    //$$             return true;
    //$$     }
    //$$     return false;
    //$$ }
    //$$
    //$$ public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //$$     for (ConfigItem<?> configItem : configs) {
    //$$         configItem.mouseClicked(mouseX, mouseY, button);
    //$$     }
    //$$     mouseScrolled(mouseX, mouseY, 0, 0); // update scroll if option changes screen size
    //$$     return false;
    //$$ }
    //#endif

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Mth.clamp(scrollOffset + (int) (verticalAmount * 15), -calculateSectionHeight(), 0);
        return true;
    }

    public int calculateSectionHeight() {
        int visibleHeight = this.height;
        int sectionSize = getTotalSectionSize();
        if (sectionSize <= visibleHeight) return 0;
        return sectionSize - visibleHeight;
    }

    public void render(GuiGraphics context, int startY, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int runningY = scrollOffset + startY + 5;
        for (ConfigItem<?> configItem : configs) {
            // if (runningY + configItem.getSize() > 55 && runningY < 55 + height)
            configItem.render(context, 20, runningY, mouseX, mouseY, delta);
            runningY += configItem.getSize() + 3;
        }

    }

    public void render2(GuiGraphics context, int startY, int mouseX, int mouseY, float delta) {
        int runningY = scrollOffset + startY + 5;
        for (ConfigItem<?> configItem : configs) {
            configItem.render2(context, 20, runningY, mouseX, mouseY, delta);
            runningY += configItem.getSize() + 3;
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
    }
}