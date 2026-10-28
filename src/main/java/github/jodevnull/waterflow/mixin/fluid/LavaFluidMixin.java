package github.jodevnull.waterflow.mixin.fluid;

import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.WaterSystem;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.utils.WaterEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public class LavaFluidMixin
{
    @Inject(method = "randomTick", at = @At("HEAD"))
    private void onLavaRandomTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom, CallbackInfo ci) {
        if (!Config.lavaCooling() || pLevel.isClientSide() || !Config.isEnabled(pLevel)) {
            return;
        }

        final var lava = pLevel.getBlockState(pPos);
        final int time = FluidProperty.timeof(lava);

        if (!lava.is(Blocks.LAVA) || time == 0) {
            return;
        }

        if (time < FluidProperty.MAX_TIME) {
            final var newState = lava.trySetValue(FluidProperty.TIME, time + 1);
            WaterSystem.of(pLevel).ifPresent(manager -> manager.setBlockState(pPos, newState));
        } else {
            pLevel.setBlockAndUpdate(pPos, Config.lavaCoolingBlock());
            WaterEffects.fizz((ServerLevel) pLevel, pPos);
        }
    }
}
