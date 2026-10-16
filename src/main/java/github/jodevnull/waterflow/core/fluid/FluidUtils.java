package github.jodevnull.waterflow.core.fluid;

import github.jodevnull.waterflow.core.water.WaterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.MAX_TIME;
import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

public final class FluidUtils
{
    public static boolean isWater(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }

    public static boolean isWater(FluidState state) {
        return isWater(state.getType());
    }

    public static boolean isLava(Fluid fluid) {
        return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
    }

    public static FluidState getWater(int level) {
        if (level == 8) {
            return Fluids.WATER.getSource(false).setValue(TIME, MAX_TIME);
        } else if (level > 0) {
            return Fluids.WATER.getFlowing(level, false).setValue(TIME, MAX_TIME);
        }

        return Fluids.EMPTY.defaultFluidState();
    }

    public static FluidState getFluidState(BlockGetter blockGetter, BlockPos pos) {
        final BlockState state = blockGetter.getBlockState(pos);

        if (state.isAir()) {
            return Fluids.EMPTY.defaultFluidState();
        }

        if (state.is(Blocks.WATER)) {
            return FluidProperty.getFluidState(state);
        }

        return blockGetter.getFluidState(pos);
    }

    public static boolean isFiniteWater(FluidState fluid) {
        return isWater(fluid) && FluidProperty.isFinite(fluid);
    }

    public static boolean isFiniteWater(BlockGetter blockGetter, BlockPos pos) {
        return isFiniteWater(getFluidState(blockGetter, pos));
    }

    public static boolean cannotFlow(WaterManager accessor, BlockPos pos) {
        for (final var dir : Direction.values()) {
            if (accessor.isNotFull(pos.relative(dir))) {
                return false;
            }
        }

        return true;
    }
}
