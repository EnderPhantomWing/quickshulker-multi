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

//#if MC >= 26.1
//$$ import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
//#else

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
//#endif
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.QuickShulkerData;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickAction;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class BundleHelper {
    public static boolean shouldAttemptBundle(Player player, ClickAction clickType, ItemStack hostStack, ItemStack insertStack, boolean enabledInConfig) {
        return (enabledInConfig && clickType == ClickAction.SECONDARY && Util.isOpenableItem(hostStack) && isAcceptedInsertItem(insertStack) && Util.getQuickItemInventory(player, hostStack) != null);
    }

    public static boolean shouldAttemptUnBundle(Player player, ClickAction clickType, ItemStack hostStack, ItemStack insertStack, boolean enabledInConfig) {
        Container stackInv = Util.getQuickItemInventory(player, hostStack);
        if (stackInv != null) {
            return (enabledInConfig && clickType == ClickAction.SECONDARY && hostStack.getCount() == 1 && !stackInv.isEmpty() && insertStack.isEmpty());
        }
        return false;
    }

    public static boolean shouldAttemptTransfer(Player player, ClickAction clickType, ItemStack hostStack, ItemStack insertStack, boolean enabledInConfig) {
        return enabledInConfig && clickType == ClickAction.SECONDARY && ShulkerUtils.isShulkerItem(hostStack) && hostStack.getCount() == 1 && Util.getQuickItemInventory(player, hostStack) != null && isAcceptedTransferItem(player, insertStack);
    }

    private static boolean isAcceptedInsertItem(ItemStack insertStack) {
        return !insertStack.isEmpty() && !ShulkerUtils.isShulkerItem(insertStack);
    }

    private static boolean isAcceptedTransferItem(Player player, ItemStack insertStack) {
        return ShulkerUtils.isShulkerItem(insertStack) && insertStack.getCount() == 1 && Util.getQuickItemInventory(player, insertStack) != null;
    }

    public static void bundleItemIntoStack(Player player, ItemStack hostStack, ItemStack insertStack, CallbackInfoReturnable<Boolean> cir) {
        if (bundleItem(player, hostStack, insertStack) != null && cir != null)
            cir.setReturnValue(true);
    }

    public static void bundleItemIntoStack(Player player, ItemStack hostStack, ItemStack insertStack, Slot slot, CallbackInfoReturnable<Boolean> cir) {
        if (bundleItem(player, hostStack, insertStack, slot) != null && cir != null) {
            cir.setReturnValue(true);
        }
    }

    public static void unbundleStackIntoSlot(Player player, ItemStack hostStack, Slot unbundleSlot, CallbackInfoReturnable<Boolean> cir) {
        ItemStack output = unbundleItem(player, hostStack, unbundleSlot);
        if (output != null) {
            unbundleSlot.setByPlayer(output);
            cir.setReturnValue(true);
        }
    }

    public static void transferItemsToShulker(Player player, ItemStack hostStack, ItemStack insertStack, CallbackInfoReturnable<Boolean> cir) {
        SimpleContainer source = (SimpleContainer) Util.getQuickItemInventory(player, insertStack);
        SimpleContainer target = (SimpleContainer) Util.getQuickItemInventory(player, hostStack);
        if (source != null && target != null) {
            int temp = 0;
            for (int i = source.getContainerSize() - 1; i >= 0; i--) {
                ItemStack stack = source.getItem(i);
                if (!stack.isEmpty() && target.canAddItem(stack)) {
                    ItemStack output = target.addItem(stack);
                    source.setItem(i, output);
                    temp++;
                }
            }
            if (temp > 0 && cir != null) {
                target.stopOpen(player);
                source.stopOpen(player);
                cir.setReturnValue(true);
            }
        }
    }

    public static ItemStack unbundleItem(Player player, ItemStack hostStack, Slot unbundleSlot) {
        Container inv = Util.getQuickItemInventory(player, hostStack);
        ItemStack output = null;
        assert inv != null;
        for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
            output = inv.getItem(i);
            if (!output.isEmpty() && unbundleSlot.mayPlace(output)) {
                output = inv.removeItemNoUpdate(i);
                inv.stopOpen(player);
                return output;
            }
        }
        return null;
    }

//    private static ItemStack bundleItem(PlayerEntity player, ItemStack hostStack, ItemStack insertStack) {
//        Inventory bundlingInv = Util.getQuickItemInventory(player, hostStack);
//        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(hostStack.getItem());
//        if (bundlingInv != null && qsdata.canBundleInsertItem(player, bundlingInv, hostStack, insertStack)) {
//            try (Transaction transaction = Transaction.openOuter()) {
//                long amount = InventoryStorage.of(bundlingInv, null).insert(ItemVariant.of(insertStack), insertStack.getCount(), transaction);
//                if (amount == 0) return null;
//                transaction.commit();
//                insertStack.decrement((int) amount);
//                bundlingInv.onClose(player);
//                return insertStack;
//            }
//        }
//        return null;
//    }

    private static ItemStack bundleItem(Player player, ItemStack hostStack, ItemStack insertStack) {
        Container bundlingInv = Util.getQuickItemInventory(player, hostStack);
        int amount = insertIntoInv(bundlingInv, player, hostStack, insertStack);
        if (amount != 0) {
            insertStack.shrink(amount);
            return insertStack;
        }
        return null;
    }

    private static ItemStack bundleItem(Player player, ItemStack hostStack, ItemStack insertStack, Slot slot) {
        if (!slot.mayPickup(player)) return null;
        Container bundlingInv = Util.getQuickItemInventory(player, hostStack);
        int amount = insertIntoInv(bundlingInv, player, hostStack, insertStack);
        if (amount != 0) {
            insertStack = slot.safeTake(amount, insertStack.getCount(), player);
            return insertStack;
        }
        return null;
    }

    private static int insertIntoInv(Container bundlingInv, Player player, ItemStack hostStack, ItemStack insertStack) {
        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(hostStack.getItem());
        int amount = 0;
        if (bundlingInv != null && qsdata.canBundleInsertItem(player, bundlingInv, hostStack, insertStack)) {
            try (Transaction transaction = Transaction.openOuter()) {
                //#if MC >= 26.1
                //$$ amount = (int) ContainerStorage.of(bundlingInv, null).insert(ItemVariant.of(insertStack), insertStack.getCount(), transaction);
                //#else
                amount = (int) InventoryStorage.of(bundlingInv, null).insert(ItemVariant.of(insertStack), insertStack.getCount(), transaction);
                //#endif
                transaction.commit();
                bundlingInv.stopOpen(player);
                return amount;
            }
        }
        return amount;
    }
}
