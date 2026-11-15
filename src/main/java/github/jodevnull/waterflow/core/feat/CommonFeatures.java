package github.jodevnull.waterflow.core.feat;

import github.jodevnull.waterflow.compat.ModCompat;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.IWaterSystem;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

public final class CommonFeatures
{
    public static void waterEvaporation(IWaterSystem system, BlockPos pos) {
        if (system.getWorld().isRaining()) {
            return;
        }

        final var fluid = FluidUtils.getFluidState(system.getSections(), pos);

        // probability of evaporation during night is lower
        if (system.getWorld().isNight() && system.getWorld().random.nextFloat() < 0.15f) {
            return;
        }

        if (FluidUtils.isFiniteWater(system.getSections(), pos)) {
            if (system.isFull(pos) || fluid.isEmpty()) {
                return;
            }

            final int time = FluidProperty.timeof(fluid);

            if (time == 1) {
                system.setWater(pos, fluid.getAmount() - 1);
            } else if (time > 0) {
                final BlockState blockState = system.getSections().getBlockState(pos);

                if (blockState.is(Blocks.WATER)) {
                    system.getSections().setBlockState(pos, blockState.setValue(TIME, time - 1));
                } else if (ModCompat.Fluidlogged.isFluidloggable(blockState)) {
                    system.getSections().setFluidState(pos, fluid.setValue(TIME, time - 1));
                }
            }
        }
    }

    public static void rainFlooding(IWaterSystem system, BlockPos pos) {
        if (!system.getWorld().isRainingAt(pos)) return;

        if (FluidUtils.isNaturalWater(system.getSections(), pos.below())) {
            return;
        }

        int intensity = system.getWorld().isThundering()
            ? Config.rainFloodingIntensity()
            : Config.rainWaterIntensity();

        if (system.getWaterLevel(pos) > -1) {
            system.addWater(pos, intensity);
        }
    }
}
