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

package net.kyrptonaught.shulkerutils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import org.jetbrains.annotations.NotNull;
//#if MC >= 1.21.10
import net.minecraft.world.entity.ContainerUser;
//#else
//#endif

import java.util.Objects;


public class ItemStackInventory extends SimpleContainer {
    protected final ItemStack itemStack;
    protected final int SIZE;

    public ItemStackInventory(ItemStack stack, int SIZE) {
        super(getStacks(stack, SIZE).toArray(new ItemStack[SIZE]));
        itemStack = stack;
        this.SIZE = SIZE;
    }

    public static NonNullList<ItemStack> getStacks(ItemStack usedStack, int SIZE) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        Objects.requireNonNull(usedStack.getComponents().get(DataComponents.CONTAINER)).copyInto(itemStacks);
        return itemStacks;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < getContainerSize(); i++) {
            itemStacks.set(i, getItem(i));
        }
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(itemStacks));
    }

    @Override
    //#if MC >= 1.21.10
    public void stopOpen(@NotNull ContainerUser user) {
    //#else
    //$$ public void stopOpen(@NotNull Player playerEntity) {
    //#endif
        if (itemStack.getCount() > 1) {
            int count = itemStack.getCount();
            itemStack.setCount(1);
            //#if MC >= 1.21.10
            ((Player) user).addItem(new ItemStack(itemStack.getItem(), count - 1));
            //#else
            //$$ playerEntity.addItem(new ItemStack(itemStack.getItem(), count - 1));
            //#endif
        }
        setChanged();
    }
}