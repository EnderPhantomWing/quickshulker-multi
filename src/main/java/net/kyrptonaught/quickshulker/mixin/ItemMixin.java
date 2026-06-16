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

package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.util.BundleHelper;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.network.QuickBundlePacket;
import net.kyrptonaught.shulkerutils.ShulkerUtils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickAction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    public void QS$onClicked(ItemStack hostStack, ItemStack insertStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (BundleHelper.shouldAttemptBundle(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingInsert)) {
            if (ShulkerUtils.isShulkerItem(hostStack) || !player.level().isClientSide()) {
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, cir);
            } else if (slot.container instanceof Inventory && ClientUtil.isCreativeScreen(player)) {//stupid creative menu shiz
                QuickBundlePacket.sendPacket(ClientUtil.getPlayerInvSlot(player.containerMenu, slot), insertStack);
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, cir);
            }
        } else if (BundleHelper.shouldAttemptTransfer(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingTransfer)) {
            BundleHelper.transferItemsToShulker(player, hostStack, insertStack, cir);
        }
    }

    @Inject(method = "overrideStackedOnOther", at = @At("HEAD"), cancellable = true)
    public void QS$onStackClicked(ItemStack hostStack, Slot slot, ClickAction clickType, Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack insertStack = slot.getItem();
        if (BundleHelper.shouldAttemptBundle(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingPickup)) {//bundle stack into held item
            if (ShulkerUtils.isShulkerItem(hostStack) || !player.level().isClientSide()) {
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, slot, cir);
            } else if (slot.container instanceof Inventory && ClientUtil.isCreativeScreen(player)) { //stupid creative menu shiz
                QuickBundlePacket.BundleIntoHeld.sendPacket(insertStack, hostStack, ClientUtil.getPlayerInvSlot(player.containerMenu, slot));
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, slot, cir);
                //QuickBundlePacket.sendCreativeSlotUpdate(insertStack, slot); // It doesn't seem to be doing anything
            }
        } else if (BundleHelper.shouldAttemptUnBundle(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingExtract)) {//unbundle held stack into slot
            if (ShulkerUtils.isShulkerItem(hostStack) || !player.level().isClientSide()) {
                BundleHelper.unbundleStackIntoSlot(player, hostStack, slot, cir);
            } else if (slot.container instanceof Inventory && ClientUtil.isCreativeScreen(player)) { //stupid creative menu shiz
                QuickBundlePacket.UnbundlePacket.sendPacket(ClientUtil.getPlayerInvSlot(player.containerMenu, slot), hostStack);
                BundleHelper.unbundleStackIntoSlot(player, hostStack, slot, cir);
            }
        }
    }
}