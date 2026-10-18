package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.leximon.fluidlogged.Fluidlogged;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Fluidlogged.Internal.class, remap = false, priority = 2000)
public class InternalMixin
{
    @ModifyReturnValue(method = "handleBlockRemoval", at = @At("RETURN"))
    private static BlockState handleBlockRemoval(BlockState original) {
        if (original.is(Blocks.WATER)) {
            return original.trySetValue(FluidProperty.TIME, FluidProperty.MAX_TIME);
        }

        return original;
    }
}
