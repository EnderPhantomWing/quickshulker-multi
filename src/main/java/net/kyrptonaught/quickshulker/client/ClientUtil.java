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

package net.kyrptonaught.quickshulker.client;

import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.mixin.CreativeSlotMixin;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class ClientUtil {

    public static boolean CheckAndSend(ItemStack stack, int slot) {
        if (Util.isOpenableItem(stack)) {
            SendOpenPacket(slot);
            return true;
        }
        return false;
    }

    private static void SendOpenPacket(int slot) {
        OpenShulkerPacket.sendOpenPacket(slot);
    }

    public static boolean isCreativeScreen(Player player) {
        return player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu;

    }

    public static int getSlotId(AbstractContainerMenu handler, Slot slot) {
        if (handler instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            //#if MC >= 26.2
            //$$ if (((CreativeModeInventoryScreen) Minecraft.getInstance().gui.screen()).isInventoryOpen() && slot instanceof CreativeModeInventoryScreen.SlotWrapper) {
            //#else
            if (Minecraft.getInstance().screen != null && ((CreativeModeInventoryScreen) Minecraft.getInstance().screen).isInventoryOpen() && slot instanceof CreativeModeInventoryScreen.SlotWrapper) {
                //#endif
                return ((CreativeSlotMixin) slot).getTarget().index;
            }
        }
        return slot.index;
    }

    public static int getPlayerInvSlot(AbstractContainerMenu handler, Slot slot) {
        if (handler instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            //#if MC >= 26.2
            //$$ if (((CreativeModeInventoryScreen) Minecraft.getInstance().gui.screen()).isInventoryOpen() && slot instanceof CreativeModeInventoryScreen.SlotWrapper) {
            //#else
            if (Minecraft.getInstance().screen != null && ((CreativeModeInventoryScreen) Minecraft.getInstance().screen).isInventoryOpen() && slot instanceof CreativeModeInventoryScreen.SlotWrapper) {
                //#endif
                return ((CreativeSlotMixin) slot).getTarget().getContainerSlot();
            }
        }
        return slot.getContainerSlot();
    }
}
