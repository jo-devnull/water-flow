package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import com.bawnorton.mixinsquared.TargetHandler;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FlowingFluid.class, priority = 2000)
public class FlowingFluidMixin
{
    @TargetHandler(name = "redirectTickSetBlock", mixin = "de.leximon.fluidlogged.mixin.classes.world_interaction.FlowingFluidMixin")
    @Redirect(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", ordinal = 1, target = "Lde/leximon/fluidlogged/mixin/extensions/LevelExtension;setFluid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;I)Z"))
    private boolean redirectSetFluid(LevelExtension instance, BlockPos blockPos, FluidState fluidState, int flags) {
        return instance.setFluid(blockPos, fluidState.trySetValue(FluidProperty.TIME, FluidProperty.MAX_TIME), flags);
    }
}
