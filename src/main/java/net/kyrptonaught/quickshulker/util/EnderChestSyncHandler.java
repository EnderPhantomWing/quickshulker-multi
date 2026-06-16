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

import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderChestSyncHandler {

    public static void syncOnContainerOpened(ServerPlayer player, ChestMenu chestMenu) {
        syncEnderChestContent(player);
        chestMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(@NotNull AbstractContainerMenu handler, int slotId, @NotNull ItemStack stack) {
                Slot slot = handler.getSlot(slotId);
                if (slot.container == player.getEnderChestInventory()) {
                    EnderChestS2CSyncPacket.S2CEChestSlotPacket.send(player, slot.getContainerSlot(), stack);
                }
            }

            @Override
            public void dataChanged(@NotNull AbstractContainerMenu handler, int property, int value) {

            }
        });
    }

    public static void syncEnderChestContent(ServerPlayer player) {
        EnderChestS2CSyncPacket.S2CEChestContentPacket.send(player, player.getEnderChestInventory().getItems());
    }

    public static void setEnderChestContent(Player player, List<ItemStack> itemStacks) {
        SimpleContainer enderChestInventory = player.getEnderChestInventory();
        // safeguard against mods only changing ender chest size on one side
        int size = Math.min(itemStacks.size(), enderChestInventory.getContainerSize());
        for (int i = 0; i < size; i++) {
            enderChestInventory.setItem(i, itemStacks.get(i));
        }
    }

}
