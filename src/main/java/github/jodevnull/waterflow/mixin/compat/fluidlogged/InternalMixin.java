package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.leximon.fluidlogged.Fluidlogged;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Fluidlogged.Internal.class, remap = false, priority = 2000)
public class InternalMixin
{
    @Redirect(method = "handleBlockRemoval", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private static FluidState redirectGetFluidState(Level instance, BlockPos pPos) {
        return FluidUtils.getFluidState(instance, pPos);
    }

    @ModifyReturnValue(method = "handleBlockRemoval", at = @At("RETURN"))
    private static BlockState handleBlockRemoval(BlockState original) {
        if (original.is(Blocks.WATER)) {
            return original.trySetValue(FluidProperty.TIME, Config.getWaterEvapTime());
        }

        return original;
    }
}
