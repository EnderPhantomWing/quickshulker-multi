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

import net.kyrptonaught.kyrptconfig.config.screen.ConfigScreen;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigSection;
import net.kyrptonaught.kyrptconfig.config.screen.items.BooleanItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.KeybindItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.SubItem;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigMenu {
    public static Screen getModConfigMenu(Screen screen) {
        ConfigOptions options = QuickShulkerMod.getConfig();

        ConfigScreen configScreen = new ConfigScreen(screen, Component.translatable("key.quickshulker.config.category.title"));
        configScreen.setSavingEvent(() -> QuickShulkerMod.config.save());

        ConfigSection activationSection = new ConfigSection(configScreen, Component.translatable("key.quickshulker.config.category.activation"));
        activationSection.addConfigItem(new KeybindItem(Component.translatable("key.quickshulker.config.keybinding"), options.keybinding.rawKey, ConfigOptions.defualtKeybind).setSaveConsumer(value -> options.keybinding.setRaw(value)));
        activationSection.addConfigItem(new KeybindItem(Component.translatable("key.quickshulker.config.openSettingGui"), options.openSettingGui.rawKey, options.openSettingGui.defaultKey).setSaveConsumer(value -> options.openSettingGui.setRaw(value)));
        activationSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.keybind"), options.keybind, true).setSaveConsumer(value -> options.keybind = value));
        activationSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.rightClick"), options.rightClickToOpen, true).setSaveConsumer(value -> options.rightClickToOpen = value));
        activationSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.keybindInInv"), options.keybingInInv, true).setSaveConsumer(value -> options.keybingInInv = value));
        activationSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.rightClickInInv"), options.rightClickInv, true).setSaveConsumer(value -> options.rightClickInv = value));

        ConfigSection optionsSection = new ConfigSection(configScreen, Component.translatable("key.quickshulker.config.category.options"));
        optionsSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.rightClickClose"), options.rightClickClose, false).setSaveConsumer(value -> options.rightClickClose = value));
        @SuppressWarnings("unchecked")
        SubItem<Boolean> subItem = (SubItem<Boolean>) optionsSection.addConfigItem(new SubItem<>(Component.translatable("key.quickshulker.config.category.bundleing"), true));
        subItem.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.supportsBundlingInsert"), options.supportsBundlingInsert, true).setSaveConsumer(value -> options.supportsBundlingInsert = value));
        subItem.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.supportsBundlingPickup"), options.supportsBundlingPickup, true).setSaveConsumer(value -> options.supportsBundlingPickup = value));
        subItem.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.supportsBundlingTransfer"), options.supportsBundlingTransfer, true).setSaveConsumer(value -> options.supportsBundlingTransfer = value));
        subItem.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.supportsBundlingExtract"), options.supportsBundlingExtract, true).setSaveConsumer(value -> options.supportsBundlingExtract = value));
        subItem.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.supportsMouseDragged"), options.supportsMouseDragged, true).setSaveConsumer(value -> options.supportsMouseDragged = value));

        ConfigSection enabledSection = new ConfigSection(configScreen, Component.translatable("key.quickshulker.config.category.enabled"));
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickShulkerBox"), options.quickShulkerBox, true).setSaveConsumer(value -> options.quickShulkerBox = value).setRequiresRestart());
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickCraftingTable"), options.quickCraftingTables, true).setSaveConsumer(value -> options.quickCraftingTables = value).setRequiresRestart());
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickStonecutter"), options.quickStonecutter, true).setSaveConsumer(value -> options.quickStonecutter = value).setRequiresRestart());
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickEChest"), options.quickEChest, true).setSaveConsumer(value -> options.quickEChest = value).setRequiresRestart());
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickSmithingTable"), options.quickSmithingTable, true).setSaveConsumer(value -> options.quickSmithingTable = value).setRequiresRestart());
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickLoom"), options.quickLoom, true).setSaveConsumer(value -> options.quickLoom = value).setRequiresRestart());
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickAnvil"), options.quickAnvil, true).setSaveConsumer(value -> options.quickAnvil = value).setRequiresRestart());
        enabledSection.addConfigItem(new BooleanItem(Component.translatable("key.quickshulker.config.quickGrindstone"), options.quickGrindstone, true).setSaveConsumer(value -> options.quickGrindstone = value).setRequiresRestart());

        return configScreen;
    }
}
