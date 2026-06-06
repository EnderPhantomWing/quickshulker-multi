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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class OpenInventoryPacket implements CustomPacketPayload {

    //#if MC <= 1.20.6
    //$$ public static final ResourceLocation OPEN_INV = ResourceLocation.tryBuild(QuickShulkerMod.MOD_ID, "open_inv");
    //#elseif MC >= 1.21.11
    public static final Identifier OPEN_INV = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "open_inv");
    //#else
    //$$ public static final ResourceLocation OPEN_INV = ResourceLocation.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "open_inv");
    //#endif

    public static final Type<OpenInventoryPacket> OPEN_INV_ID = new CustomPacketPayload.Type<>(OPEN_INV);

    public static final StreamCodec<FriendlyByteBuf, OpenInventoryPacket> CODEC = StreamCodec.ofMember(OpenInventoryPacket::write, buf -> new OpenInventoryPacket());

    public static void send(ServerPlayer player) {
        ServerPlayNetworking.send(player, new OpenInventoryPacket());
    }

    private void write(FriendlyByteBuf buf) {}

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return OPEN_INV_ID;
    }

}
