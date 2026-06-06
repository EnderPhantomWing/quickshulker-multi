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

package net.kyrptonaught.quickshulker.util;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.mixin.AbstractContainerScreenInvoker;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
//#if MC >= 26.1
//$$ import net.minecraft.world.inventory.ContainerInput;
//#else
import net.minecraft.world.inventory.ClickType;
//#endif

//#if MC >= 1.21.10
import net.minecraft.client.input.MouseButtonEvent;
//#else
//#endif

import java.util.Set;

public class MouseDraggedHandler {
    private static final Set<Slot> DRAGGED_SLOTS = Sets.newHashSet();
    private static DragMode dragMode;

    public static boolean canInsertIntoContainer(Player player, ItemStack hostStack, ItemStack insertStack) {
        Container inv = Util.getQuickItemInventory(player, hostStack);
        if (inv == null) return false;
        for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
            ItemStack pickStack = inv.getItem(i);
            if (pickStack.isEmpty() || (ItemStack.isSameItemSameComponents(pickStack, insertStack) && pickStack.getCount() < pickStack.getMaxStackSize()))
                return true;
        }
        return false;
    }

    public static boolean isContainerEmpty(Player player, ItemStack hostStack) {
        Container inv = Util.getQuickItemInventory(player, hostStack);
        if (inv != null) {
            return inv.isEmpty();
        }
        return true;
    }

    //#if MC >= 1.21.10
    public static boolean beforeMouseClick(AbstractContainerScreen<?> screen, MouseButtonEvent click){
    //#else
    //$$ public static boolean beforeMouseClick(AbstractContainerScreen<?> screen, double mouseX, double mouseY, int button) {
    //#endif
        if (!QuickShulkerMod.getConfig().supportsMouseDragged) return false;
        //#if MC >= 1.21.10
        Slot slot = ((AbstractContainerScreenInvoker) screen).QS$getSlotAt(click.x(), click.y());
        if(slot != null && click.button() == 1){
        //#else
        //$$ Slot slot = ((AbstractContainerScreenInvoker) screen).QS$getSlotAt(mouseX, mouseY);
        //$$ if (slot != null && button == 1) {
        //#endif
            Minecraft client = ((ScreenAccessor) screen).getClient();
            ItemStack itemStack = screen.getMenu().getCarried();
            Container inv = Util.getQuickItemInventory(client.player, itemStack);
            if (inv == null) return false;
            if (slot.hasItem()) {
                dragMode = DragMode.BUNDLE;
            } else {
                dragMode = DragMode.UNBUNDLE;
            }
            DRAGGED_SLOTS.clear();
            return true;
        }
        return false;
    }

    //#if MC >= 1.21.10
    public static boolean beforeMouseDragged(AbstractContainerScreen<?> screen, MouseButtonEvent click){
    //#else
    //$$ public static boolean beforeMouseDragged(AbstractContainerScreen<?> screen, double mouseX, double mouseY, int button) {
    //#endif
        if (!QuickShulkerMod.getConfig().supportsMouseDragged) return false;
        boolean result = false;
        if (dragMode != null) {
            Minecraft client = ((ScreenAccessor) screen).getClient();
            AbstractContainerMenu handler = screen.getMenu();
            ItemStack itemStack = handler.getCarried();
            //#if MC >= 1.21.10
            if(click.button() != 1){
            //#else
            //$$ if (button != 1) {
            //#endif
                dragMode = null;
                DRAGGED_SLOTS.clear();
                return false;
            }
            //#if MC >= 1.21.10
            Slot slot = ((AbstractContainerScreenInvoker) screen).QS$getSlotAt(click.x(), click.y());
            //#else
            //$$ Slot slot = ((AbstractContainerScreenInvoker) screen).QS$getSlotAt(mouseX, mouseY);
            //#endif
            if (client.player != null && slot != null && (handler.canDragTo(slot) || slot.mayPickup(client.player))) {
                if (dragMode == DragMode.BUNDLE) {
                    if (slot.hasItem() && canInsertIntoContainer(client.player, itemStack, slot.getItem()) && !ShulkerUtils.isShulkerItem(slot.getItem()) && !DRAGGED_SLOTS.contains(slot)) {
                        DRAGGED_SLOTS.add(slot);
                        //#if MC >= 26.1
                        //$$ ((AbstractContainerScreenInvoker) screen).QS$onMouseClick(slot, slot.index, click.button(), ContainerInput.PICKUP);
                        //#elseif MC >= 1.21.10
                        ((AbstractContainerScreenInvoker) screen).QS$onMouseClick(slot, slot.index, click.button(), ClickType.PICKUP);
                        //#else
                        //$$ ((AbstractContainerScreenInvoker) screen).QS$onMouseClick(slot, slot.index, button, ClickType.PICKUP);
                        //#endif
                        result = true;
                    }
                } else {
                    if (!slot.hasItem() && !isContainerEmpty(client.player, itemStack) && !DRAGGED_SLOTS.contains(slot)) {
                        DRAGGED_SLOTS.add(slot);
                        //#if MC >= 26.1
                        //$$ ((AbstractContainerScreenInvoker) screen).QS$onMouseClick(slot, slot.index, click.button(), ContainerInput.PICKUP);
                        //#elseif MC >= 1.21.10
                        ((AbstractContainerScreenInvoker) screen).QS$onMouseClick(slot, slot.index, click.button(), ClickType.PICKUP);
                        //#else
                        //$$ ((AbstractContainerScreenInvoker) screen).QS$onMouseClick(slot, slot.index, button, ClickType.PICKUP);
                        //#endif
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    //#if MC >= 1.21.10
    public static boolean beforeMouseReleased(AbstractContainerScreen<?> screen, MouseButtonEvent click){
    //#else
    //$$ public static boolean beforeMouseReleased(AbstractContainerScreen<?> screen, double mouseX, double mouseY, int button) {
    //#endif
        if (!QuickShulkerMod.getConfig().supportsMouseDragged) return false;
        if (dragMode != null) {
            dragMode = null;
            //#if MC >= 1.21.10
            if(click.button() == 1 && !DRAGGED_SLOTS.isEmpty()){
            //#else
            //$$ if (button == 1 && !DRAGGED_SLOTS.isEmpty()) {
            //#endif
                DRAGGED_SLOTS.clear();
                return true;
            }
        }
        return false;
    }

    public static void beforeDrawForeground(AbstractContainerScreen<?> screen, GuiGraphics context, int mouseX, int mouseY) {
        AbstractContainerMenu handler = screen.getMenu();
        for (Slot slot : handler.slots) {
            if (DRAGGED_SLOTS.contains(slot)) {
                context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80FFFFFF);
            }
        }
    }

    private enum DragMode {
        BUNDLE,
        UNBUNDLE
    }
}
