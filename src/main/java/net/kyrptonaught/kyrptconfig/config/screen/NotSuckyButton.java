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

//#if MC >= 1.21.11
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
//#elseif MC >= 1.21.6
//$$ import net.minecraft.util.ARGB;
//$$ import net.minecraft.client.renderer.RenderPipelines;
//#elseif MC >= 1.21.2
//$$ import net.minecraft.util.ARGB;
//$$ import net.minecraft.client.renderer.RenderType;
//#else
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
//#if MC <= 1.21.10
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import org.jetbrains.annotations.NotNull;

public class NotSuckyButton extends Button {
    int buttonColor = -1;
    public boolean disableHover = false;
    //#if MC <= 1.20.6
    //$$ private static final WidgetSprites TEXTURES = new WidgetSprites(ResourceLocation.tryParse("widget/button"), ResourceLocation.tryParse("widget/button_disabled"), ResourceLocation.tryParse("widget/button_highlighted"));
    //$$ public NotSuckyButton(int x, int y, int width, int height, Component message, OnPress onPress) {
    //#elseif MC >= 1.21.11
    private static final WidgetSprites TEXTURES = new WidgetSprites(Identifier.parse("widget/button"), Identifier.parse("widget/button_disabled"), Identifier.parse("widget/button_highlighted"));
    public NotSuckyButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, OnPress onPress) {
    //#else
    //$$ private static final WidgetSprites TEXTURES = new WidgetSprites(ResourceLocation.parse("widget/button"), ResourceLocation.parse("widget/button_disabled"), ResourceLocation.parse("widget/button_highlighted"));
    //$$ public NotSuckyButton(int x, int y, int width, int height, Component message, OnPress onPress) {
    //#endif
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    public void setButtonColor(int color) {
        //#if MC >= 1.21.11
        this.setMessage(ComponentUtils.mergeStyles(this.getMessage(), Style.EMPTY.withColor(color)));
        //#endif
        this.buttonColor = color;
    }

    public boolean detectHover(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }

    @Override
    //#if MC >= 1.21.11
    protected void renderContents(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
    //#else
    //$$ public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
    //#endif
        //This can fix text rendering over the wrong btn
        //context.getMatrices().translate(0, 0,  1);

        if (disableHover) isHovered = false;

        //#if MC >= 1.21.11
        this.renderDefaultSprite(context);
        this.renderDefaultLabel(context.textRenderer());
        //#elseif MC >= 1.21.6
        //$$ context.blitSprite(
        //$$         RenderPipelines.GUI_TEXTURED,
        //$$         TEXTURES.get(this.active, this.isHoveredOrFocused()),
        //$$         this.getX(),
        //$$         this.getY(),
        //$$         this.getWidth(),
        //$$         this.getHeight(),
        //$$         ARGB.white(this.alpha));
        //$$ //
        //$$ Font textRenderer = Minecraft.getInstance().font;
        //$$ int i = ARGB.color(this.alpha, this.active ? buttonColor : -6250336);
        //$$ renderString(context, textRenderer, i);
        //#elseif MC >= 1.21.2
        //$$ context.blitSprite(
        //$$         RenderType::guiTextured,
        //$$         TEXTURES.get(this.active, this.isHoveredOrFocused()),
        //$$         this.getX(),
        //$$         this.getY(),
        //$$         this.getWidth(),
        //$$         this.getHeight(),
        //$$         ARGB.white(this.alpha));
        //$$ //
        //$$ Font textRenderer = Minecraft.getInstance().font;
        //$$ int i = this.active ? buttonColor : 0xA0A0A0;
        //$$ renderString(context, textRenderer, i);
        //#else
        //$$ context.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        //$$ RenderSystem.enableBlend();
        //$$ RenderSystem.enableDepthTest();
        //$$ context.blitSprite(TEXTURES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        //$$ context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //$$ Font textRenderer = Minecraft.getInstance().font;
        //$$ int i = this.active ? buttonColor : 0xA0A0A0;
        //$$ renderString(context, textRenderer, i);
        //#endif
    }
}
