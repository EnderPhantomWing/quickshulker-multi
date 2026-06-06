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

package net.kyrptonaught.quickshulker.compat.reinfshulker;

//TODO: 等待Reinforced Shulker Box更新
//#if MC >= 26.1
//#else

import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.ReinforcedShulkerBoxBlock;
import atonkish.reinfshulker.block.entity.ModBlockEntityType;
import net.kyrptonaught.quickshulker.api.ItemStackInventory;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
//#endif

import java.util.Objects;
import java.util.function.BiConsumer;

public class ReinfshulkerOpenableRegistry {

    //#if MC >= 26.1
    //#else
    private static final BiConsumer<Player, ItemStack> REINFORCED_SHULKER_BOX_CONSUMER = (Player player, ItemStack stack) -> {
        ReinforcedShulkerBoxBlock block = (ReinforcedShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock();
        ReinforcingMaterial material = block.getMaterial();
        ItemStackInventory inventory = new ItemStackInventory(stack, material.getSize());
        String namespace = Objects.requireNonNull(BlockEntityType.getKey(ModBlockEntityType.REINFORCED_SHULKER_BOX_MAP.get(material))).getNamespace();

        MenuConstructor screenHandlerFactory = (int syncId, Inventory playerInventory, Player playerEntity) ->
                ReinforcedStorageScreenHandler.createShulkerBoxScreen(material, syncId, playerInventory, inventory);
        Component text = stack.getComponents().has(
                DataComponents.CUSTOM_NAME) ? stack.getHoverName() : Component.translatable("container." + namespace + "." + material.getName() + "ShulkerBox"
        );

        player.openMenu(new SimpleMenuProvider(screenHandlerFactory, text));
    };

    public static void registerProviders() {
        new QuickOpenableRegistry.Builder()
                .setItem(ReinforcedShulkerBoxBlock.class)
                .supportsBundleing(true)
                .setOpenAction(REINFORCED_SHULKER_BOX_CONSUMER)
                .register();
    }
    //#endif
}
