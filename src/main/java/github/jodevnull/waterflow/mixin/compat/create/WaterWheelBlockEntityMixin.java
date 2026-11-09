package github.jodevnull.waterflow.mixin.compat.create;

import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WaterWheelBlockEntity.class)
public class WaterWheelBlockEntityMixin
{
    @Redirect(method = "getFlowVectorAtPosition", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getFlow(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 redirectGetFlow(FluidState fluid, BlockGetter pLevel, BlockPos pPos) {
        if (FluidUtils.isFiniteWater(fluid) && pLevel.getBlockState(pPos.below()).isAir()) {
            return new Vec3(0, -1, 0);
        }

        return fluid.getFlow(pLevel, pPos);
    }
}
