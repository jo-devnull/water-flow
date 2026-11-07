package github.jodevnull.waterflow.core.feat;

import github.jodevnull.waterflow.compat.Compat;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.WaterManager;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

public final class CommonFeatures
{
    public static void waterEvaporation(WaterManager water, BlockPos pos) {
        if (water.world.isRaining()) {
            return;
        }

        final var fluid = FluidUtils.getFluidState(water, pos);

        // probability of evaporation during night is lower
        if (water.world.isNight() && water.world.random.nextFloat() < 0.15f) {
            return;
        }

        if (FluidUtils.isFiniteWater(water, pos)) {
            if (water.isFull(pos) || fluid.isEmpty()) {
                return;
            }

            final int time = FluidProperty.timeof(fluid);

            if (time == 1) {
                water.setWater(pos, fluid.getAmount() - 1);
            } else if (time > 0) {
                final BlockState blockState = water.getBlockState(pos);

                if (blockState.is(Blocks.WATER)) {
                    water.setBlockState(pos, blockState.setValue(TIME, time - 1));
                } else if (Compat.Fluidlogged.isFluidloggable(blockState)) {
                    water.setFluidState(pos, fluid.setValue(TIME, time - 1));
                }
            }
        }
    }

    public static void rainFlooding(WaterManager water, BlockPos pos) {
        if (!water.world.isRainingAt(pos)) return;

        if (FluidUtils.isNaturalWater(water, pos.below())) {
            return;
        }

        int intensity = water.world.isThundering()
            ? Config.rainFloodingIntensity()
            : Config.rainWaterIntensity();

        if (water.getWaterLevel(pos) > -1) {
            water.addWater(pos, Utils.clamp(intensity, 0, 8));
        }
    }
}
