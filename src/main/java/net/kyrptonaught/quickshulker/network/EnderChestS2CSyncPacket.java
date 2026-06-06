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

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.util.PacketUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderChestS2CSyncPacket {

    public record S2CEChestContentPacket(List<ItemStack> itemStacks) implements CustomPacketPayload {

        //#if MC <= 1.20.6
        //$$ public static final Type<S2CEChestContentPacket> S2C_ECHEST_CONTENT_PACKET_ID = new Type<>(ResourceLocation.tryBuild(QuickShulkerMod.MOD_ID, "s2c_echest_content_packet"));
        //#elseif MC >= 1.21.11
        public static final Type<S2CEChestContentPacket> S2C_ECHEST_CONTENT_PACKET_ID = new Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "s2c_echest_content_packet"));
        //#else
        //$$ public static final Type<S2CEChestContentPacket> S2C_ECHEST_CONTENT_PACKET_ID = new Type<>(ResourceLocation.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "s2c_echest_content_packet"));
        //#endif
        public static final StreamCodec<RegistryFriendlyByteBuf, S2CEChestContentPacket> CODEC = StreamCodec.composite(ItemStack.OPTIONAL_LIST_STREAM_CODEC, S2CEChestContentPacket::itemStacks, S2CEChestContentPacket::new);

        public static void send(ServerPlayer player, List<ItemStack> itemStacks) {
            ServerPlayNetworking.send(player, new S2CEChestContentPacket(itemStacks));
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return S2C_ECHEST_CONTENT_PACKET_ID;
        }
    }

    public record S2CEChestSlotPacket(int slotId, ItemStack itemStack) implements CustomPacketPayload {
        //#if MC <= 1.20.6
        //$$ public static final Type<S2CEChestSlotPacket> S2C_ECHEST_SLOT_PACKET_ID = new Type<>(ResourceLocation.tryBuild(QuickShulkerMod.MOD_ID, "s2c_echest_slot_packet"));
        //#else
        public static final Type<S2CEChestSlotPacket> S2C_ECHEST_SLOT_PACKET_ID = new Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "s2c_echest_slot_packet"));
        //#endif
        public static final StreamCodec<RegistryFriendlyByteBuf, S2CEChestSlotPacket> CODEC = StreamCodec.ofMember(
                (value, buf) -> {
                    buf.writeInt(value.slotId);
                    PacketUtils.writeItemStack(buf, value.itemStack);
                },
                buf -> new S2CEChestSlotPacket(buf.readInt(), PacketUtils.readItemStack(buf)));

        public static void send(ServerPlayer player, int slotId, ItemStack itemStack) {
            ServerPlayNetworking.send(player, new S2CEChestSlotPacket(slotId, itemStack));
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return S2C_ECHEST_SLOT_PACKET_ID;
        }
    }
}
