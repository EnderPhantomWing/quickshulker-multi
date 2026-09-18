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

package net.kyrptonaught.kyrptconfig.keybinding;

import lib.blue.endless.jankson.JsonElement;
import lib.blue.endless.jankson.JsonPrimitive;
import net.kyrptonaught.kyrptconfig.config.CustomMarshaller;
import net.kyrptonaught.kyrptconfig.config.CustomSerializable;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
//#if MC < 26.3
import org.lwjgl.glfw.GLFW;
//#else
//$$ import org.lwjgl.sdl.SDLKeyboard;
//$$ import org.lwjgl.sdl.SDLMouse;
//#endif

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class CustomKeyBinding implements CustomSerializable {
    private final String MOD_ID;
    protected Consumer<CustomKeyBinding> keyUpdate;
    public boolean unknownIsActivated = false;
    public String rawKey = "";
    public String defaultKey = "";
    public InputConstants.Key parsedKey;
    public boolean doParseKey = true;
    boolean holding = false;

    public CustomKeyBinding(String MOD_ID) {
        this.MOD_ID = MOD_ID;
    }

    public CustomKeyBinding(String MOD_ID, boolean unknownIsActivated) {
        this.unknownIsActivated = unknownIsActivated;
        this.MOD_ID = MOD_ID;
    }

    public static CustomKeyBinding configDefault(String MOD_ID, String defaultKey) {
        CustomKeyBinding customKeyBinding = new CustomKeyBinding(MOD_ID).setRaw(defaultKey);
        customKeyBinding.defaultKey = defaultKey;
        return customKeyBinding;
    }

    public CustomKeyBinding setRaw(String key) {
        rawKey = key;
        this.runConsumer(this);
        doParseKey = true;
        holding = false;
        return this;
    }

    public boolean wasPressed() {
        boolean pressed = isKeybindPressed();
        if (!holding) {
            holding = pressed;
            return pressed;
        }
        if (!pressed)
            holding = false;
        return false;
    }

    private void parseKeycode() {
        if (doParseKey) {
            parsedKey = getKeybinding().orElse(null);
            doParseKey = false;
        }
    }

    public boolean isKeybindPressed() {
        parseKeycode();
        if (parsedKey == null) // Invalid key
            return false;
        if (parsedKey == InputConstants.UNKNOWN)
            return unknownIsActivated; // Always pressed for empty or explicitly "key.keyboard.unknown"
        boolean pressed;
        if (parsedKey.getType() == InputConstants.Type.MOUSE)
            //#if MC >= 26.3
            //$$ pressed = (SDLMouse.SDL_GetMouseState(null, null) & parsedKey.getValue()) == 1;
            //#elseif MC >= 1.21.10
            pressed = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().handle(), parsedKey.getValue()) == 1;
            //#else
            //$$ pressed = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), parsedKey.getValue()) == 1;
            //#endif
        else
            //#if MC >= 26.3
            //$$ pressed = Objects.requireNonNull(SDLKeyboard.SDL_GetKeyboardState()).get(parsedKey.getValue()) == 1;
            //#elseif MC >= 1.21.10
            pressed = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().handle(), parsedKey.getValue()) == 1;
            //#else
            //$$ pressed = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), parsedKey.getValue()) == 1;
            //#endif
        return pressed;
    }

    public boolean matches(int keyCode, InputConstants.Type type) {
        parseKeycode();
        if (parsedKey == null) return false;
        return parsedKey.getType() == type && parsedKey.getValue() == keyCode;
    }

    public Optional<InputConstants.Key> getKeybinding() {
        if (rawKey.isEmpty())
            return Optional.of(InputConstants.UNKNOWN);
        try {
            return Optional.of(InputConstants.getKey(rawKey));
        } catch (IllegalArgumentException e) {
            System.out.println(MOD_ID + ": unknown key entered");
            return Optional.empty();
        }
    }

    public InputConstants.Key getDefaultKey() {
        if (defaultKey == null || defaultKey.isEmpty())
            return InputConstants.UNKNOWN;
        try {
            return InputConstants.getKey(defaultKey);
        } catch (IllegalArgumentException e) {
            System.out.println(MOD_ID + ": unknown default key entered");
            return InputConstants.UNKNOWN;
        }
    }

    public void setConsumer(Consumer<CustomKeyBinding> keyUpdate){
        this.keyUpdate = keyUpdate;
    }

    public void runConsumer(CustomKeyBinding key){
        if(this.keyUpdate != null){
            keyUpdate.accept(this);
        }
    }

    @Override
    public JsonElement toJson(CustomMarshaller m) {
        return new JsonPrimitive(this.rawKey);
    }

    @Override
    public CustomSerializable fromJson(CustomMarshaller m, JsonElement obj, Class<CustomSerializable> clazz) {
        if (obj instanceof JsonPrimitive string)
            setRaw(string.asString());
        return this;
    }
}
