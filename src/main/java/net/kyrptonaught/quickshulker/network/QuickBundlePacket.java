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

package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.util.BundleHelper;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.util.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.Slot;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record QuickBundlePacket(int slotId, ItemStack stackToBundle) implements CustomPacketPayload {

    //#if MC <= 1.20.6
    //$$ private static final ResourceLocation QUICK_BUNDLE_PACKET = ResourceLocation.tryBuild(QuickShulkerMod.MOD_ID, "quick_bundle_packet");
    //#elseif MC >= 1.21.11
    private static final Identifier QUICK_BUNDLE_PACKET = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundle_packet");
    //#else
    //$$ private static final ResourceLocation QUICK_BUNDLE_PACKET = ResourceLocation.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundle_packet");
    //#endif
    private static final Type<QuickBundlePacket> QUICK_BUNDLE_PACKET_ID = new Type<>(QUICK_BUNDLE_PACKET);
    private static final StreamCodec<RegistryFriendlyByteBuf, QuickBundlePacket> CODEC = StreamCodec.ofMember(
            (value, buf) -> {
                buf.writeInt(value.slotId);
                PacketUtils.writeItemStack(buf, value.stackToBundle);
            },
            buf -> new QuickBundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

    public static void registerReceivePacket() {
        PayloadTypeRegistry.playS2C().register(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, QuickBundlePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, QuickBundlePacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, (payload, context) -> {
            if (context.player().isCreative()) {
                //#if MC >= 1.21.4
                context.server().execute(() -> BundleHelper.bundleItemIntoStack(context.player(), context.player().getInventory().getItem(payload.slotId), payload.stackToBundle, null));
                //#else
                //$$ context.server().execute(() -> BundleHelper.bundleItemIntoStack(context.player(), context.player().getInventory().getItem(payload.slotId), payload.stackToBundle, null));
                //#endif
            }
        });
        UnbundlePacket.registerReceivePacket();
        BundleIntoHeld.registerReceivePacket();
    }

    @Environment(EnvType.CLIENT)
    public static void sendPacket(int slotID, ItemStack stackToBundle) {
        ClientPlayNetworking.send(new QuickBundlePacket(slotID, stackToBundle.copy()));
    }

    public static void sendCreativeSlotUpdate(ItemStack output, Slot slot) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(output, slot.index);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return QUICK_BUNDLE_PACKET_ID;
    }

    public record BundleIntoHeld(List<ItemStack> stackList, int slotId) implements CustomPacketPayload {

        //#if MC <= 1.20.6
        //$$ private static final ResourceLocation QUICK_BUNDLEHELD_PACKET = ResourceLocation.tryBuild(QuickShulkerMod.MOD_ID, "quick_bundleheld_packet");
        //#elseif MC >= 1.21.11
        private static final Identifier QUICK_BUNDLEHELD_PACKET = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundleheld_packet");
        //#else
        //$$ private static final ResourceLocation QUICK_BUNDLEHELD_PACKET = ResourceLocation.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundleheld_packet");
        //#endif
        private static final Type<BundleIntoHeld> QUICK_BUNDLEHELD_PACKET_ID = new Type<>(QUICK_BUNDLEHELD_PACKET);
        private static final StreamCodec<RegistryFriendlyByteBuf, BundleIntoHeld> CODEC = StreamCodec.composite(ItemStack.OPTIONAL_LIST_STREAM_CODEC, BundleIntoHeld::stackList, ByteBufCodecs.INT, BundleIntoHeld::slotId, BundleIntoHeld::new);

        public static void registerReceivePacket() {
            PayloadTypeRegistry.playS2C().register(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, BundleIntoHeld.CODEC);
            PayloadTypeRegistry.playC2S().register(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, BundleIntoHeld.CODEC);
            ServerPlayNetworking.registerGlobalReceiver(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, (payload, context) -> {
                if (context.player().isCreative()) {
                    context.server().execute(() -> {
                        Slot slot = context.player().containerMenu.getSlot(payload.slotId);
                        BundleHelper.bundleItemIntoStack(context.player(), payload.stackList.get(1), payload.stackList.get(0), slot, null);
                    });
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(ItemStack stackToBundle, ItemStack bundleStack, int slotId) {
            ClientPlayNetworking.send(new BundleIntoHeld(List.of(stackToBundle.copy(), bundleStack), slotId));
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return QUICK_BUNDLEHELD_PACKET_ID;
        }
    }

    public record UnbundlePacket(int slotId, ItemStack unbundleStack) implements CustomPacketPayload {

        //#if MC <= 1.20.6
        //$$ private static final ResourceLocation QUICK_UNBUNDLE_PACKET = ResourceLocation.tryBuild(QuickShulkerMod.MOD_ID, "quick_unbundle_packet");
        //#elseif MC >= 1.21.11
        private static final Identifier QUICK_UNBUNDLE_PACKET = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_unbundle_packet");
        //#else
        //$$ private static final ResourceLocation QUICK_UNBUNDLE_PACKET = ResourceLocation.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_unbundle_packet");
        //#endif
        private static final Type<UnbundlePacket> QUICK_UNBUNDLE_PACKET_ID = new Type<>(QUICK_UNBUNDLE_PACKET);
        private static final StreamCodec<RegistryFriendlyByteBuf, UnbundlePacket> CODEC = StreamCodec.ofMember(
                (value, buf) -> {
                    buf.writeInt(value.slotId);
                    PacketUtils.writeItemStack(buf, value.unbundleStack);
                },
                buf -> new UnbundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

        public static void registerReceivePacket() {
            PayloadTypeRegistry.playS2C().register(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, UnbundlePacket.CODEC);
            PayloadTypeRegistry.playC2S().register(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, UnbundlePacket.CODEC);
            ServerPlayNetworking.registerGlobalReceiver(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, (payload, context) -> {
                if (context.player().isCreative()) {
                    int playerInvSlotID = payload.slotId;
                    ItemStack unBundleStack = payload.unbundleStack;
                    context.server().execute(() -> {
                        Slot unbundleSlot = context.player().containerMenu.getSlot(payload.slotId);
                        ItemStack output = BundleHelper.unbundleItem(context.player(), unBundleStack, unbundleSlot);
                        if (output != null)
//                            context.player().getInventory().setStack(playerInvSlotID, output);
                            unbundleSlot.setByPlayer(output);
                    });
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(int slotID, ItemStack unBundleStack) {
            ClientPlayNetworking.send(new UnbundlePacket(slotID, unBundleStack));
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return QUICK_UNBUNDLE_PACKET_ID;
        }
    }
}
