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

//#if MC >= 1.21.6
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
//#elseif MC >= 1.21.2
//$$ import net.minecraft.util.ARGB;
//$$ import net.minecraft.client.renderer.RenderType;
//#else
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
//#if MC >= 1.21.10
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#endif
//#if MC >= 1.21.11
//#endif

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    int selectedSection = 0;
    List<ConfigSection> sections = new ArrayList<>();
    Screen previousScreen;
    int horizontalScrollOffset = -1;
    private Runnable saveRunnable;
    private NotSuckyButton scrollLeftBTN, scrollRightBTN;
    //#if MC <= 1.20.6
    //$$ private static final ResourceLocation SCROLLER_TEXTURE = ResourceLocation.tryParse("widget/scroller");
    //$$ private static final ResourceLocation OPTIONS_BACKGROUND_TEXTURE = ResourceLocation.tryParse("textures/block/dirt.png");
    //#elseif MC >= 1.21.11
    private static final Identifier SCROLLER_TEXTURE = Identifier.parse("widget/scroller");
    private static final Identifier OPTIONS_BACKGROUND_TEXTURE = Identifier.parse("textures/block/dirt.png");
    //#else
    //$$ private static final ResourceLocation SCROLLER_TEXTURE = ResourceLocation.parse("widget/scroller");
    //$$ private static final ResourceLocation OPTIONS_BACKGROUND_TEXTURE = ResourceLocation.parse("textures/block/dirt.png");
    //#endif

    public ConfigScreen(Screen previousScreen, Component title) {
        super(title);
        this.previousScreen = previousScreen;
    }

    protected void init() {
        int center = this.width / 2;
        this.addRenderableWidget(new NotSuckyButton(center - 153, height - 25, 150, 20, Component.translatable("key.kyrptconfig.config.exit"), widget -> {
            this.minecraft.setScreen(previousScreen);
        }));

        this.addRenderableWidget(new NotSuckyButton(center + 3, height - 25, 150, 20, Component.translatable("key.kyrptconfig.config.saveExit"), widget -> {
            save();
            this.minecraft.setScreen(previousScreen);
        }));
        for (ConfigSection section : sections) {
            //#if MC >= 1.21.11
            section.init(width, height - 57 - 30);
            //#else
            //$$ if (minecraft != null) {
            //$$     section.init(minecraft, width, height - 57 - 30);
            //$$ }
            //#endif
        }

        adjustForHorizontalScroll(this.width);
    }

    public void setSavingEvent(Runnable save) {
        this.saveRunnable = save;
    }

    public void save() {
        for (ConfigSection section : sections) {
            section.save();
        }
        if (saveRunnable != null)
            saveRunnable.run();
    }

    public void addConfigSection(ConfigSection item) {
        item.selectionIndex = sections.size();
        if (sections.isEmpty())
            item.sectionSelectionBTN.setX(10);
        else
            item.sectionSelectionBTN.setX(sections.getLast().sectionSelectionBTN.getX() + sections.getLast().sectionSelectionBTN.getWidth() + 3);
        item.sectionSelectionBTN.setWidth(Minecraft.getInstance().font.width(item.title) + 10);

        this.sections.add(item);
    }

    public boolean adjustForHorizontalScroll(int maxWidth) {
        NotSuckyButton lastBTN = sections.getLast().sectionSelectionBTN;

        this.scrollLeftBTN = new NotSuckyButton(10, 32, 10, 20, Component.literal("<"), widget -> {
            NotSuckyButton nextBtn = sections.getFirst().sectionSelectionBTN;
            for (int i = sections.size() - 1; i >= 0; i--) {
                if (sections.get(i).sectionSelectionBTN.getX() < scrollLeftBTN.getX() + scrollLeftBTN.getWidth() + 3) {
                    nextBtn = sections.get(i).sectionSelectionBTN;
                    if (i == 0)
                        scrollLeftBTN.active = false;
                    break;
                }
            }
            scrollRightBTN.active = true;
            horizontalScrollOffset += (nextBtn.getX()) - (scrollLeftBTN.getX() + scrollLeftBTN.getWidth() + 3);
        });

        this.scrollRightBTN = new NotSuckyButton(this.width - 20, 32, 10, 20, Component.literal(">"), widget -> {
            NotSuckyButton nextBtn = lastBTN;
            for (int i = 0; i < sections.size(); i++) {
                if (sections.get(i).sectionSelectionBTN.getX() + sections.get(i).sectionSelectionBTN.getWidth() + 3 > maxWidth) {
                    nextBtn = sections.get(i).sectionSelectionBTN;
                    if (i == sections.size() - 1)
                        scrollRightBTN.active = false;
                    break;
                }
            }
            scrollLeftBTN.active = true;
            horizontalScrollOffset += (nextBtn.getX() + nextBtn.getWidth() + 3) - (scrollRightBTN.getX());
        });

        if (lastBTN.getX() + lastBTN.getWidth() + 3 > maxWidth) {
            for (ConfigSection section : sections) {
                section.sectionSelectionBTN.setX(section.sectionSelectionBTN.getX() + scrollLeftBTN.getWidth() + 3);
            }

            scrollLeftBTN.visible = true;
            scrollRightBTN.visible = true;
            scrollLeftBTN.active = sections.getFirst().sectionSelectionBTN.getX() < scrollLeftBTN.getRight() + 3;
            scrollRightBTN.active = lastBTN.getRight() + 3 > scrollRightBTN.getX();
            horizontalScrollOffset = 0;
            return true;
        }

        scrollLeftBTN.active = false;
        scrollLeftBTN.visible = false;

        scrollRightBTN.active = false;
        scrollRightBTN.visible = false;

        horizontalScrollOffset = -1;
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigSection section : sections) {
            section.tick();
        }
    }

    public void setSelectedSection(int selectedSection) {
        this.selectedSection = selectedSection;
    }

    @Override
    //#if MC >= 1.21.10
    public boolean keyPressed(@NotNull KeyEvent input) {
        if (sections.get(selectedSection).keyPressed(input)) return true;
        return super.keyPressed(input);
    }
    //
    @Override
    public boolean charTyped(@NotNull CharacterEvent input) {
        return sections.get(selectedSection).charTyped(input);
    }
    //
    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent click, boolean doubled) {
        super.mouseClicked(click, doubled);
    //
        if (scrollLeftBTN.mouseClicked(click, doubled) || scrollRightBTN.mouseClicked(click, doubled))
            return true;
    //
        for (ConfigSection section : sections)
            if (section.sectionSelectionBTN.mouseClicked(click, doubled)) return true;
    //
        return sections.get(selectedSection).mouseClicked(click, doubled);
    }
    //#else
    //$$ public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //$$     if (sections.get(selectedSection).keyPressed(keyCode, scanCode, modifiers)) return true;
    //$$     return super.keyPressed(keyCode, scanCode, modifiers);
    //$$ }
    //$$
    //$$ @Override
    //$$ public boolean charTyped(char chr, int modifiers) {
    //$$     return sections.get(selectedSection).charTyped(chr, modifiers);
    //$$ }
    //$$
    //$$ @Override
    //$$ public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //$$     super.mouseClicked(mouseX, mouseY, button);
    //$$
    //$$     if (scrollLeftBTN.mouseClicked(mouseX, mouseY, button) || scrollRightBTN.mouseClicked(mouseX, mouseY, button))
    //$$         return true;
    //$$
    //$$     for (ConfigSection section : sections)
    //$$         if (section.sectionSelectionBTN.mouseClicked(mouseX, mouseY, button)) return true;
    //$$
    //$$     return sections.get(selectedSection).mouseClicked(mouseX, mouseY, button);
    //$$ }
    //#endif

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return sections.get(selectedSection).mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);

        ConfigSection section = sections.get(selectedSection);

        context.enableScissor(0, 57, this.width, this.height - 30);
        context.fillGradient(0, 57, this.width, this.height, 1744830464, 1744830464);
        section.render(context, 57, mouseX, mouseY, deltaTicks);
        context.disableScissor();

        context.drawCenteredString(this.font, this.title, this.width / 2, 13, -1);
        drawHeaderAndFooterSeparators(context);

        boolean noHover = scrollLeftBTN.detectHover(mouseX, mouseY) | scrollRightBTN.detectHover(mouseX, mouseY);
        if (horizontalScrollOffset > -1) {
            context.enableScissor(scrollLeftBTN.getRight() + 1, scrollLeftBTN.getY(), scrollRightBTN.getX() - 1, scrollRightBTN.getBottom());
            for (int i = 0; i < sections.size(); i++) {
                NotSuckyButton selectionBTN = sections.get(i).sectionSelectionBTN;
                selectionBTN.active = i != selectedSection;
                if (i == 0) {
                    selectionBTN.setX(scrollLeftBTN.getX() + scrollLeftBTN.getWidth() + 3 - horizontalScrollOffset);
                } else {
                    NotSuckyButton previousBTN = sections.get(i - 1).sectionSelectionBTN;
                    selectionBTN.setX(previousBTN.getX() + previousBTN.getWidth() + 3);
                }
                selectionBTN.disableHover = noHover;
                selectionBTN.render(context, mouseX, mouseY, deltaTicks);
            }
            context.disableScissor();
            scrollLeftBTN.render(context, mouseX, mouseY, deltaTicks);
            scrollRightBTN.render(context, mouseX, mouseY, deltaTicks);
        } else {
            for (int i = 0; i < sections.size(); i++) {
                NotSuckyButton selectionBTN = sections.get(i).sectionSelectionBTN;
                selectionBTN.active = i != selectedSection;
                selectionBTN.render(context, mouseX, mouseY, deltaTicks);
            }
        }

        if (section.calculateSectionHeight() > 0) {
            int x = this.width - 6;

            float overflow = ((float) section.calculateSectionHeight() / this.height);
            int height = section.height - Mth.lerpInt(overflow, 0, section.height);
            height = Mth.clamp(height, 20, section.height - 8);

            float percentage = (float) -section.scrollOffset / section.calculateSectionHeight();
            int y = Mth.lerpInt(percentage, 57, this.height - 30 - height);

            context.fill(x, 57, x + 6, this.height - 30, -16777216);
            //#if MC >= 1.21.8
            context.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, x, y, 6, height);
            //#elseif MC >= 1.21.2
            //$$ context.blitSprite(RenderType::guiTextured, SCROLLER_TEXTURE, x, y, 6, height);
            //#else
            //$$ context.blitSprite(SCROLLER_TEXTURE, x, y, 6, height);
            //#endif
        }

        section.render2(context, 57, mouseX, mouseY, deltaTicks);
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
    }

    //#if MC >= 1.21.6
    private void renderBackgroundTexture(GuiGraphics context) {
        context.blit(RenderPipelines.GUI_TEXTURED, OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, 32, 32);
    }
    //
    private void drawHeaderAndFooterSeparators(GuiGraphics context) {
        context.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, 55, 0.0f, 0.0f, this.width, 2, 32, 2);
        context.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height -30, 0.0f, 0.0f, this.width, 2, 32, 2);
    }
    //
    private void drawDirtTextureBlurred(GuiGraphics context, int x, int y, int width, int height) {
        int color = ARGB.colorFromFloat(.7f, 0, 0, 0);
        context.blit(RenderPipelines.GUI_TEXTURED, OPTIONS_BACKGROUND_TEXTURE, x, y, 0, 0, width, height, 64, 64);
        context.fillGradient(x, y, x + width, y + height, color, color);
    }
    //#elseif MC >= 1.21.2
    //$$ private void renderBackgroundTexture(GuiGraphics context) {
    //$$     context.blit(RenderType::guiTextured, OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, 32, 32);
    //$$ }
    //$$ //
    //$$ private void drawHeaderAndFooterSeparators(GuiGraphics context) {
    //$$     context.blit(RenderType::guiTextured, Screen.HEADER_SEPARATOR, 0, 55, 0.0f, 0.0f, this.width, 2, 32, 2);
    //$$     context.blit(RenderType::guiTextured, Screen.FOOTER_SEPARATOR, 0, this.height -30, 0.0f, 0.0f, this.width, 2, 32, 2);
    //$$ }
    //$$ //
    //$$ private void drawDirtTextureBlurred(GuiGraphics context, int x, int y, int width, int height) {
    //$$     int color = ARGB.colorFromFloat(.7f, 0, 0, 0);
    //$$     context.blit(RenderType::guiTextured, OPTIONS_BACKGROUND_TEXTURE, x, y, 0, 0, width, height, 64, 64);
    //$$     context.fillGradient(x, y, x + width, y + height, color, color);
    //$$ }
    //#else
    //$$ private void renderBackgroundTexture(GuiGraphics context) {
    //$$     context.blit(OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, 32, 32);
    //$$ }
    //$$
    //$$ private void drawHeaderAndFooterSeparators(GuiGraphics context) {
    //$$     RenderSystem.enableBlend();
    //$$     context.blit(Screen.HEADER_SEPARATOR, 0, 55, 0.0f, 0.0f, this.width, 2, 32, 2);
    //$$     context.blit(Screen.FOOTER_SEPARATOR, 0, this.height - 30, 0.0f, 0.0f, this.width, 2, 32, 2);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //$$
    //$$ private void drawDirtTextureBlurred(GuiGraphics context, int x, int y, int width, int height) {
    //$$     context.setColor(0.25F, 0.25F, 0.25F, 1.0F);
    //$$     context.blit(OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
    //$$     context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    //$$ }
    //#endif
}
