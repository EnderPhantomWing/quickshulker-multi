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

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.mixin.minecraft.AbstractContainerScreenInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
//#if MC < 26.3
import org.lwjgl.glfw.GLFW;
//#else
//$$ import org.lwjgl.sdl.SDLMouse;
//#endif

/**
 * Puts the cursor back on the item the player was pointing at when a quick switch is triggered.
 * <p>
 * Restoring the raw screen position is not enough: the player inventory panel slides sideways when the recipe
 * book is open, so the same coordinates land on a neighbouring slot and the item has to be re-found by hand.
 * Instead the player-inventory slot index is remembered and re-resolved against the slots of whichever screen
 * opens next, so the cursor follows the item regardless of layout.
 */
@Environment(EnvType.CLIENT)
public final class CursorFollower {

    private CursorFollower() {
    }

    /**
     * Must run after the target screen finished initialising — subclasses such as
     * {@code InventoryScreen} (via {@code AbstractRecipeBookScreen}) only assign {@code leftPos} after
     * calling {@code super.init()}, so the slot coordinates are not final while still inside init.
     */
    public static void snap(AbstractContainerScreen<?> screen) {
        AbstractContainerScreenInvoker invoker = (AbstractContainerScreenInvoker) screen;

        if (QuickShulkerMod.lastCursorSlot >= 0) {
            int target = QuickShulkerMod.lastCursorSlot;
            for (Slot slot : invoker.QS$getMenu().slots) {
                if (slot.container instanceof Inventory && slot.getContainerSlot() == target) {
                    Window window = Minecraft.getInstance().getWindow();
                    double scaleX = (double) window.getScreenWidth() / window.getGuiScaledWidth();
                    double scaleY = (double) window.getScreenHeight() / window.getGuiScaledHeight();
                    warp((invoker.QS$getLeftPos() + slot.x + 8) * scaleX,
                         (invoker.QS$getTopPos() + slot.y + 8) * scaleY);
                    clear();
                    return;
                }
            }
            QuickShulkerMod.lastCursorSlot = -1;
        }

        // No matching slot in this screen (the item is not in the player inventory here), so fall back to
        // the previous behaviour and restore the raw screen position.
        if (QuickShulkerMod.lastMouseX != 0 || QuickShulkerMod.lastMouseY != 0) {
            warp(QuickShulkerMod.lastMouseX, QuickShulkerMod.lastMouseY);
            clear();
        }
    }

    private static void clear() {
        QuickShulkerMod.lastCursorSlot = -1;
        QuickShulkerMod.lastMouseX = 0;
        QuickShulkerMod.lastMouseY = 0;
    }

    /** Moves the OS cursor to a position given in window coordinates (not GUI-scaled ones). */
    private static void warp(double windowX, double windowY) {
        //#if MC >= 26.3
        //$$ SDLMouse.SDL_WarpMouseInWindow(Minecraft.getInstance().getWindow().handle(), (float) windowX, (float) windowY);
        //#elseif MC >= 1.21.10
        GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().handle(), windowX, windowY);
        //#else
        //$$ GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), windowX, windowY);
        //#endif
    }
}
