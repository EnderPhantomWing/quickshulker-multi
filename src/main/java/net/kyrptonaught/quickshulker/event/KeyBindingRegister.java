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

import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
//#if MC >= 26.1
//$$ import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//#else
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//#endif
//#if MC >= 1.21.11
import net.minecraft.resources.Identifier;
//#elseif MC >=1.21.10
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//#if MC >= 1.21.10
import net.minecraft.client.KeyMapping;
//#endif

public class KeyBindingRegister {
    //#if MC >= 1.21.11
    public static final KeyMapping.Category MAIN = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "main"));
    //#elseif MC >= 1.21.10
    //$$ public static final KeyMapping.Category MAIN = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "main"));
    //#else
    //$$ public static final String MAIN = "key.categories.quickshulker";
    //#endif

    public static void register() {
        //#if MC >= 26.1
        //$$ KeyMappingHelper.registerKeyMapping(new DisplayOnlyKeyBind(
        //#else
        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
        //#endif
                "key.quickshulker.config.openSettingGui",
                MAIN,
                QuickShulkerMod.getConfig().openSettingGui,
                setKey -> QuickShulkerMod.config.save()
        ));
        //#if MC >= 26.1
        //$$ KeyMappingHelper.registerKeyMapping(new DisplayOnlyKeyBind(
        //#else
        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
        //#endif
                "key.quickshulker.config.keybinding",
                MAIN,
                QuickShulkerMod.getConfig().keybinding,
                setKey -> QuickShulkerMod.config.save()
        ));
    }
}
