package github.jodevnull.waterflow.mixin.fluid;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.water.WaterManager;
import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid
{
    @Inject(method = "createFluidStateDefinition", at=@At("TAIL"))
    private void appendProperties(StateDefinition.Builder<Fluid, FluidState> builder, CallbackInfo ci) {
        if ((Object) this instanceof WaterFluid) {
            builder.add(FluidProperty.TIME);
        }

        else if ((Object) this instanceof LavaFluid) {
            builder.add(FluidProperty.TIME);
        }
    }

    @Inject(method = "spread", at=@At("HEAD"), cancellable = true)
    private void tryFlowingWater(Level pLevel, BlockPos pos, FluidState pState, CallbackInfo ci) {
        final var fluidState = FluidUtils.getFluidState(pLevel, pos);

        if (FluidUtils.isFiniteWater(fluidState)) {
            if (WaterSystem.run(pLevel, pos)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "getNewLiquid", at = @At("HEAD"), cancellable = true)
    private void getUpdatedState(Level pLevel, BlockPos pPos, BlockState pBlockState, CallbackInfoReturnable<FluidState> cir) {
        final var fluidState = FluidUtils.getFluidState(pLevel, pPos);

        if (FluidUtils.isFiniteWater(fluidState)) {
            cir.setReturnValue(Fluids.WATER.getFlowing(fluidState.getAmount(), false));
        }
    }

    @Inject(method = "getFlow", at = @At("HEAD"), cancellable = true)
    private void getFlow(BlockGetter pBlockReader, BlockPos pPos, FluidState pFluidState, CallbackInfoReturnable<Vec3> cir) {
        final var fluidState = FluidUtils.getFluidState(pBlockReader, pPos);

        if (FluidUtils.isFiniteWater(fluidState)) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }

    @WrapMethod(method = "canConvertToSource(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", remap = false)
    private boolean canConvertToSource(FluidState state, Level level, BlockPos pos, Operation<Boolean> original) {
        if (Config.isEnabled(level) && FluidUtils.isWater(state)) {
            final var fluid = FluidUtils.getFluidState(level, pos);
            return FluidProperty.timeof(fluid) == 0;
        }

        return original.call(state, level, pos);
    }
}
