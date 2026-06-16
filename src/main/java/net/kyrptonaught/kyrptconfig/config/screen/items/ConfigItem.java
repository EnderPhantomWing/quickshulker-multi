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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.locale.Language;
//#if MC >= 1.21.10
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#else
//#endif

//#if MC >= 1.21.2
import net.minecraft.util.ARGB;
//#else
//$$ import net.minecraft.util.FastColor;
//#endif

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ConfigItem<T> {
    protected Consumer<T> saveConsumer, valueUpdatedEvent;
    protected NotSuckyButton resetButton;
    protected T value, defaultValue;
    private Component fieldTitle;
    private List<Component> toolTipText;
    private boolean requiresRestart = false;
    private boolean isHidden = false;

    public ConfigItem(Component name, T value, T defaultValue) {
        this.fieldTitle = name;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public ConfigItem<?> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public ConfigItem<?> setValueUpdatedEvent(Consumer<T> valueUpdatedEvent) {
        this.valueUpdatedEvent = valueUpdatedEvent;
        return this;
    }

    public ConfigItem<?> setRequiresRestart() {
        requiresRestart = true;
        ((MutableComponent) fieldTitle).append(" *");
        return this;
    }

    public Component getTitleText() {
        return fieldTitle;
    }

    public ConfigItem<?> setTitleText(Component title) {
        this.fieldTitle = title;
        return this;
    }

    public ConfigItem<?> setToolTipWithNewLine(String translatableKey) {
        String[] translated = Language.getInstance().getOrDefault(translatableKey).split("\n");
        this.toolTipText = new ArrayList<>();
        for (String line : translated) {
            this.toolTipText.add(Component.literal(line));
        }

        return this;
    }

    public ConfigItem<?> setToolTip(Component toolTip) {
        this.toolTipText = List.of(toolTip);
        return this;
    }

    public ConfigItem<?> setToolTip(Component... toolTips) {
        this.toolTipText = List.of(toolTips);
        return this;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public ConfigItem<?> setHidden(boolean hidden) {
        this.isHidden = hidden;
        return this;
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    protected void runSaveConsumer(T value) {
        if (saveConsumer != null && value != null)
            saveConsumer.accept(value);
    }

    public void save() {
        runSaveConsumer(value);
    }

    public int getSize() {
        if (isHidden) return 0;
        return getHeaderSize() + getContentSize();
    }

    public int getHeaderSize() {
        return 20;
    }

    public int getContentSize() {
        return 0;
    }

    public void useDefaultResetBTN() {
        this.resetButton = new NotSuckyButton(0, 0, 35, 20, Component.translatable("key.kyrptconfig.config.reset"), widget -> resetToDefault());
    }

    public void resetToDefault() {
        setValue(defaultValue);
    }

    public boolean isValueDefault() {
        return !value.equals(defaultValue);
    }

    public void setValue(T value) {
        this.value = value;
        if (valueUpdatedEvent != null)
            valueUpdatedEvent.accept(this.value);
    }

    public void tick() {
    }

    //#if MC >= 1.21.10
    public void mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (isHidden) return;
        if (resetButton != null)
            resetButton.mouseClicked(click, doubled);
    }
    //
    public boolean charTyped(CharacterEvent input) {
        return false;
    }
    //
    public boolean keyPressed(KeyEvent input) {
        return false;
    }
    //#else
    //$$ public void mouseClicked(double mouseX, double mouseY, int button) {
    //$$     if (isHidden) return;
    //$$     if (resetButton != null)
    //$$         resetButton.mouseClicked(mouseX, mouseY, button);
    //$$ }
    //$$
    //$$ public boolean charTyped(char chr, int modifiers) {
    //$$     return false;
    //$$ }
    //$$
    //$$ public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //$$     return false;
    //$$ }
    //#endif

    public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = y + getHeaderSize();
        if (mouseY > y && mouseY < height)
            //#if MC >= 1.21.2
            context.fill(0, y - 1, width, height + 1, ARGB.color(50, 255, 255, 255));
            //#else
            //$$ context.fill(0, y - 1, width, height + 1, FastColor.ARGB32.color(50, 255, 255, 255));
            //#endif

        context.drawString(Minecraft.getInstance().font, this.fieldTitle, x, y + 6, -1, true);

        if (resetButton != null) {
            this.resetButton.setY(y);
            this.resetButton.setX(width - resetButton.getWidth() - 20);
            resetButton.active = isValueDefault();
            resetButton.render(context, mouseX, mouseY, delta);
        }

    }

    public void render2(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;
        if (mouseX > x && mouseX < x + Minecraft.getInstance().font.width(fieldTitle) &&
                mouseY > y && mouseY < y + 12)
            renderToolTip(context, mouseX, mouseY);
    }

    public void renderToolTip(GuiGraphics context, int x, int y) {
        //#if MC >= 1.21.6
        if (toolTipText != null && requiresRestart) {
            List<Component> newList = new ArrayList<>(toolTipText);
            newList.add(Component.translatable("key.kyrptconfig.config.restartRequired"));
            context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, newList, x, y);
        } else if (toolTipText != null) {
            context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, toolTipText, x, y);
        } else if (requiresRestart) {
            context.setTooltipForNextFrame(Minecraft.getInstance().font, Component.translatable("key.kyrptconfig.config.restartRequired"), x, y);
        }
        //#else
        //$$ if (toolTipText != null && requiresRestart) {
        //$$     List<Component> newList = new ArrayList<>(toolTipText);
        //$$     newList.add(Component.translatable("key.kyrptconfig.config.restartRequired"));
        //$$     context.renderComponentTooltip(Minecraft.getInstance().font, newList, x, y);
        //$$ } else if (toolTipText != null) {
        //$$     context.renderComponentTooltip(Minecraft.getInstance().font, toolTipText, x, y);
        //$$ } else if (requiresRestart) {
        //$$     context.renderTooltip(Minecraft.getInstance().font, Component.translatable("key.kyrptconfig.config.restartRequired"), x, y);
        //$$ }
        //#endif
    }
}
