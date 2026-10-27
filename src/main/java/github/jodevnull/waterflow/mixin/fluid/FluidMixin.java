package github.jodevnull.waterflow.mixin.fluid;

import com.mojang.patchy.BlockedServers;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.mixin.extensions.ServerChunkCacheExtension;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

@Mixin(Fluid.class)
public abstract class FluidMixin
{
    @Inject(method = "isRandomlyTicking", at=@At("HEAD"), cancellable = true)
    private void isRandomlyTicking(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof WaterFluid) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick", at=@At("HEAD"))
    private void onWaterEvaporate(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom, CallbackInfo ci) {
        if (!Config.waterEvaporation() && !Config.isEnabled(pLevel) || pLevel.isClientSide()) {
            return;
        }

        final var water = WaterSystem.get((ServerLevel) pLevel);
        final var state = FluidUtils.getFluidState(water, pPos);

        if (pLevel.isRaining()) {
            return;
        }

        if (pLevel.isNight() && pLevel.random.nextFloat() < 0.15f) {
            return;
        }

        if (FluidUtils.isFiniteWater(water, pPos)) {
            if (water.isFull(pPos)) {
                return;
            }

            final int time = FluidProperty.timeof(state);

            if (time == 1) {
                water.setWater(pPos, state.getAmount() - 1);
            } else if (time > 0) {
                ((LevelExtension) pLevel).setFluid(pPos, state.setValue(TIME, time - 1),
                    Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS
                );

                if (pLevel instanceof ServerLevel level) {
                    ((ServerChunkCacheExtension) level.getChunkSource()).fluidChanged(pPos);
                    ((LevelExtension) level).sendFluidUpdated(pPos,
                        Block.UPDATE_IMMEDIATE  |
                        Block.UPDATE_CLIENTS    |
                        Block.UPDATE_KNOWN_SHAPE
                    );
                }
            }
        }
    }
}
