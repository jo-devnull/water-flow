package github.jodevnull.waterflow.mixin.world;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatMixin
{
    @Redirect(method = "isUnderwater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState fixFluidState(Level instance, BlockPos pPos) {
        return FluidUtils.getFluidState(instance, pPos);
    }

    @Inject(method = "isUnderwater", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"), cancellable = true)
    private void fixBoatPosition(CallbackInfoReturnable<Boat.Status> cir, @Local() FluidState fluidState) {
        if (FluidUtils.isFiniteWater(fluidState)) {
            // this.floatBoat();
            cir.setReturnValue(Boat.Status.IN_WATER);
        }
    }
}
