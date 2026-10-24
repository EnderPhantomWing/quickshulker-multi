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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Tells the client which player inventory slot holds the item whose menu was just opened. */
public record SetUsedSlotPacket(int playerInvSlot) implements CustomPacketPayload {

    //#if MC <= 1.20.6
    //$$ public static final Type<SetUsedSlotPacket> SET_USED_SLOT_ID = new Type<>(ResourceLocation.tryBuild(QuickShulkerMod.MOD_ID, "set_used_slot"));
    //#elseif MC >= 1.21.11
    public static final Type<SetUsedSlotPacket> SET_USED_SLOT_ID = new Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "set_used_slot"));
    //#else
    //$$ public static final Type<SetUsedSlotPacket> SET_USED_SLOT_ID = new Type<>(ResourceLocation.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "set_used_slot"));
    //#endif

    public static final StreamCodec<ByteBuf, SetUsedSlotPacket> CODEC =
            ByteBufCodecs.VAR_INT.map(SetUsedSlotPacket::new, SetUsedSlotPacket::playerInvSlot);

    public static void send(ServerPlayer player, int playerInvSlot) {
        ServerPlayNetworking.send(player, new SetUsedSlotPacket(playerInvSlot));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return SET_USED_SLOT_ID;
    }
}
