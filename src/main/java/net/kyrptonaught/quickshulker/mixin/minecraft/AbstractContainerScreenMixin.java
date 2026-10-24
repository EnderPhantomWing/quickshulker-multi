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

package net.kyrptonaught.quickshulker.mixin.minecraft;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.util.MouseDraggedHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
//#if MC >= 1.21.10
import net.minecraft.client.input.MouseButtonEvent;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(
            //#if MC >= 1.21.10
            method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z",
            //#else
            //$$ method = "mouseClicked(DDI)Z",
            //#endif
            at = @At("HEAD"),
            cancellable = true
    )
    //#if MC >= 1.21.10
    private void QS$mouseClicked(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir){
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        boolean result = MouseDraggedHandler.beforeMouseClick(screen, click);
    //#else
    //$$ private void QS$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
    //$$     AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
    //$$     boolean result = MouseDraggedHandler.beforeMouseClick(screen, mouseX, mouseY, button);
        //#endif
        if (result) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            //#if MC >= 1.21.10
            method = "mouseDragged(Lnet/minecraft/client/input/MouseButtonEvent;DD)Z",
            //#else
            //$$ method = "mouseDragged(DDIDD)Z",
            //#endif
            at = @At("HEAD"),
            cancellable = true
    )
    //#if MC >= 1.21.10
    private void QS$mouseDragged(MouseButtonEvent click, double offsetX, double offsetY, CallbackInfoReturnable<Boolean> cir){
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        boolean result = MouseDraggedHandler.beforeMouseDragged(screen, click);
    //#else
    //$$ private void QS$mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
    //$$     AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
    //$$     boolean result = MouseDraggedHandler.beforeMouseDragged(screen, mouseX, mouseY, button);
        //#endif
        if (result) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            //#if MC >= 1.21.10
            method = "mouseReleased(Lnet/minecraft/client/input/MouseButtonEvent;)Z",
            //#else
            //$$ method = "mouseReleased(DDI)Z",
            //#endif
            at = @At("HEAD"),
            cancellable = true
    )
    //#if MC >= 1.21.10
    private void QS$mouseReleased(MouseButtonEvent click, CallbackInfoReturnable<Boolean> cir){
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        boolean result = MouseDraggedHandler.beforeMouseReleased(screen, click);
    //#else
    //$$ private void QS$mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
    //$$     AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
    //$$     boolean result = MouseDraggedHandler.beforeMouseReleased(screen, mouseX, mouseY, button);
        //#endif
        if (result) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            //#if MC >= 26.1
            //$$ method = "extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
            //#elseif MC >= 1.21.6
            method = "renderContents(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            //#else
            //$$ method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 26.1
                    //$$ target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
                    //#else
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V",
                    //#endif
                    shift = At.Shift.AFTER
            )
    )
    private void QS$drawForeground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        MouseDraggedHandler.beforeDrawForeground(screen, context, mouseX, mouseY);
    }

    @Inject(
            //#if MC >= 26.4
            //$$ method = "extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;)V",
            //#elseif MC >= 26.1
            //$$ method = "extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V",
            //#elseif MC >= 1.21.11
            method = "renderSlot(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;II)V",
            //#else
            //$$ method = "renderSlot(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;)V",
            //#endif
            at = @At("HEAD")
    )
    //#if MC >= 26.4
    //$$ private void QS$drawOpenedSlotBackground(GuiGraphicsExtractor context, Slot slot, CallbackInfo ci) {
    //#elseif MC >= 1.21.11
    private void QS$drawOpenedSlotBackground(GuiGraphics context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
    //#else
    //$$ private void QS$drawOpenedSlotBackground(GuiGraphics context, Slot slot, CallbackInfo ci) {
    //#endif
        // Mark the slot holding the item whose menu is open, behind the item itself.
        if (!QuickShulkerMod.getConfig().fillOpenedBackground) return;
        int usedSlot = ((ItemInventoryContainer) ((AbstractContainerScreen<?>) (Object) this).getMenu()).QS$getUsedSlotInPlayerInv();
        if (usedSlot >= 0 && slot.container instanceof Inventory && slot.getContainerSlot() == usedSlot) {
            context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFF000000 | QuickShulkerMod.getConfig().colorBackground);
        }
    }
}
