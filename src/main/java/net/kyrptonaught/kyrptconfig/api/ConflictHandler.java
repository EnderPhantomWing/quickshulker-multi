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

package net.kyrptonaught.kyrptconfig.api;

import net.kyrptonaught.kyrptconfig.config.screen.items.ConfigItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.KeybindItem;

import java.util.ArrayList;

public class ConflictHandler {
    public static final ArrayList<KeybindItem> CUSTOM_KEYBIND_ITEMS = new ArrayList<>();

    public static void updateMap(ConfigItem<String> item) {
        if (item instanceof KeybindItem customItem) {
            for (KeybindItem keybindItem : CUSTOM_KEYBIND_ITEMS) {
                if (keybindItem.getTitleText().equals(item.getTitleText())) {
                    CUSTOM_KEYBIND_ITEMS.remove(keybindItem);
                    break;
                }
            }
            CUSTOM_KEYBIND_ITEMS.add(customItem);
            updateCustomConflicts();
        }
    }

    public static void updateCustomConflicts() {
        for (KeybindItem keybindItem : CUSTOM_KEYBIND_ITEMS) {
            keybindItem.updateMessage();
        }
    }
}
