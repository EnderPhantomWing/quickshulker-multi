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

package net.kyrptonaught.quickshulker.config;

import lib.blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.kyrptonaught.quickshulker.QuickShulkerMod;

public class ConfigOptions implements AbstractConfigFile {
    public static String defualtKeybind = "key.keyboard.k";
    @Comment("Activation key")
    public CustomKeyBinding keybinding = CustomKeyBinding.configDefault(QuickShulkerMod.MOD_ID, "key.keyboard.k");
    @Comment("Open setting gui key")
    public CustomKeyBinding openSettingGui = CustomKeyBinding.configDefault(QuickShulkerMod.MOD_ID, "key.keyboard.keypad.add");
    @Comment("Right Clicking with shulker in hand opens it")
    public boolean rightClickToOpen = true;
    @Comment("Hitting the keybind with shulker in hand opens it")
    public boolean keybind = true;
    @Comment("Hitting the keybind while hovering over shulker in inv opens it")
    public boolean keybingInInv = true;
    @Comment("Right Clicking a shulker in your inv opens it")
    public boolean rightClickInv = true;

    @Comment("Right Clicking the opened shulker in your inv closes it")
    public boolean rightClickClose = false;
    @Comment("Right Clicking a shulker with an item inserts it")
    public boolean supportsBundlingInsert = true;
    @Comment("Right Clicking an item with a shulker inserts it")
    public boolean supportsBundlingPickup = true;
    @Comment("Right Clicking a shulker with a shulker transfer items")
    public boolean supportsBundlingTransfer = true;
    @Comment("Right Clicking an empty slot with a shulker extracts an item")
    public boolean supportsBundlingExtract = true;
    @Comment("Right Clicking and Dragging with a shulker to bulk insert or extract items")
    public boolean supportsMouseDragged = true;

    @Comment("Enable opening Shulker Boxes")
    public boolean quickShulkerBox = true;
    @Comment("Enable opening Crafting Tables")
    public boolean quickCraftingTables = true;
    @Comment("Enable opening Stonecutter")
    public boolean quickStonecutter = true;
    @Comment("Enable opening EnderChest")
    public boolean quickEChest = true;
    @Comment("Enable opening Smithing Table")
    public boolean quickSmithingTable = true;
    @Comment("Enable opening Loom")
    public boolean quickLoom = true;
    @Comment("Enable opening Anvil")
    public boolean quickAnvil = true;
    @Comment("Enable opening Grindstone")
    public boolean quickGrindstone = true;

}