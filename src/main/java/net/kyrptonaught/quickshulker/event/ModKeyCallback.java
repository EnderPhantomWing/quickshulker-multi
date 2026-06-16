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

package net.kyrptonaught.quickshulker.event;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.config.ModConfigMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;

public class ModKeyCallback {

    public static void onKeyPressed(ClientLevel clientWorld) {
        Minecraft mc = Minecraft.getInstance();
        ConfigOptions configs = QuickShulkerMod.getConfig();
        if (configs.openSettingGui.wasPressed()) {
            //#if MC >= 26.2
            //$$ mc.gui.setScreen(ModConfigMenu.getModConfigMenu(mc.gui.screen()));
            //#else
            mc.setScreen(ModConfigMenu.getModConfigMenu(mc.screen));
            //#endif
        }
        if (configs.keybinding.isKeybindPressed()) {
            Player player = mc.player;
            //#if MC >= 26.2
            //$$ if (mc.gui.screen() == null && QuickShulkerMod.getConfig().keybind && player != null && !player.isSpectator()) {
            //#else
            if (mc.screen == null && QuickShulkerMod.getConfig().keybind && player != null && !player.isSpectator()) {
            //#endif
                if (player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty())
                    ClientUtil.CheckAndSend(player.getOffhandItem(), 45);
                else
                    //#if MC >= 1.21.5
                    ClientUtil.CheckAndSend(player.getMainHandItem(), 36 + player.getInventory().getSelectedSlot());
                    //#else
                    //$$ ClientUtil.CheckAndSend(player.getMainHandItem(), 36 + player.getInventory().selected);
                    //#endif
            }
        }
    }
}
