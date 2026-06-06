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

package net.kyrptonaught.quickshulker.mixin.compat.reinfshulker;

//#if MC >= 26.1
//$$ import net.minecraft.server.MinecraftServer;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ //
//$$ @Mixin(MinecraftServer.class)
//$$ public abstract class ReinforcedShulkerBoxBlockMixin {
//$$     @Inject(method = "runServer", at = @At("HEAD"))
//$$     private void onRun(CallbackInfo ci) {
//$$         System.err.println("26.1+ Reinforced Shulker Box is not yet supported.");
//$$     }
//$$ }
//#else

import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.ReinforcedShulkerBoxBlock;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.kyrptonaught.quickshulker.compat.ModIds;
import net.kyrptonaught.shulkerutils.UpgradableShulker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(require = @Condition(ModIds.reinfshulker))
@Mixin(ReinforcedShulkerBoxBlock.class)
public abstract class ReinforcedShulkerBoxBlockMixin implements UpgradableShulker {

    @Shadow(remap = false)
    public abstract ReinforcingMaterial getMaterial();

    @Override
    public int QS$getInventorySize() {
        return this.getMaterial().getSize();
    }
}
//#endif
