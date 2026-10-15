package github.jodevnull.waterflow.mixin.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.foundation.fluid.FluidHelper;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OpenEndedPipe.class)
public abstract class OpenEndedPipeMixin
{
    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    public abstract BlockPos getOutputPos();

    @Shadow
    private Level world;

    @ModifyExpressionValue(method = "removeFluidFromSpace", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
    private boolean removeSourceCheck1(boolean original) {
        return original || FluidUtils.isFiniteWater(world, getOutputPos());
    }

    @ModifyExpressionValue(method = "provideFluidToSpace", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
    private boolean removeSourceCheck2(boolean original) {
        return false;
    }

    @Inject(method = "removeFluidFromSpace", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;createLegacyBlock()Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    private void removeFluidFromSpace(boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        final var fluid = FluidUtils.getFluidState(world, getOutputPos());

        if (!FluidUtils.isWater(fluid))
            return;

        if (fluid.getAmount() > 0 && FluidUtils.isFiniteWater(fluid)) {
            if (world.setBlockAndUpdate(getOutputPos(), Blocks.AIR.defaultBlockState())) {
                cir.setReturnValue(new FluidStack(fluid.getType(), 125 * fluid.getAmount()));
            }
        }
    }

    @Redirect(method = "provideFluidToSpace", at= @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean provideFluidToSpace(Level world, BlockPos pPos, BlockState pNewState, int pFlags, @Local(argsOnly = true) FluidStack stack) {
        if (world.isClientSide() || !Config.isEnabled(world) || !FluidHelper.isWater(stack.getFluid()))
            return world.setBlock(pPos, pNewState, pFlags);

        final var water = WaterSystem.get((ServerLevel) world);
        final int newLevel = Math.floorDiv(stack.getAmount(), 125);

        if (water.getWaterLevel(pPos) >= 0) {
            water.addWater(pPos, newLevel);
            return true;
        }

        return false;
    }
}
