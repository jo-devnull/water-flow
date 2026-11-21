package github.jodevnull.waterflow.core.fluid;

import github.jodevnull.waterflow.compat.ModCompat;
import github.jodevnull.waterflow.ModTags;
import github.jodevnull.waterflow.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

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

    public static boolean isLava(FluidState state) {
        return isLava(state.getType());
    }

    public static FluidState getWater(int level) {
        if (level == 8) {
            return Fluids.WATER.getSource(false).setValue(TIME, Config.getEvaporationTime());
        } else if (level > 0) {
            return Fluids.WATER.getFlowing(level, false).setValue(TIME, Config.getEvaporationTime());
        }

        return Fluids.EMPTY.defaultFluidState();
    }

    public static FluidState getFluidState(BlockGetter blockGetter, BlockPos pos) {
        final BlockState state = blockGetter.getBlockState(pos);

        if (state.isAir()) {
            return Fluids.EMPTY.defaultFluidState();
        }

        if (state.getBlock() instanceof LiquidBlock) {
            return FluidProperty.getFluidState(state);
        }

        return blockGetter.getFluidState(pos);
    }

    public static boolean isFiniteWater(FluidState fluid) {
        return isWater(fluid) && FluidProperty.isFinite(fluid);
    }

    public static boolean isNaturalWater(FluidState fluid) {
        return isWater(fluid) && !FluidProperty.isFinite(fluid);
    }

    public static boolean isFiniteWater(BlockGetter blockGetter, BlockPos pos) {
        return isFiniteWater(getFluidState(blockGetter, pos));
    }

    public static boolean isNaturalWater(BlockGetter blockGetter, BlockPos pos) {
        return isNaturalWater(getFluidState(blockGetter, pos));
    }

    public static boolean shouldSkipVanillaBehavior(BlockGetter getter, BlockPos pos) {
        final BlockState state = getter.getBlockState(pos);
        return state.is(ModTags.Blocks.NEEDS_WATER_SOURCE);
    }

    public static int getWaterLevelOf(BlockGetter world, BlockPos pos, BlockState state) {
        if (state.is(Blocks.VOID_AIR)) {
            return -1;
        }

        if (state.isAir() || Config.isWaterBreakable(state)) {
            return 0;
        }

        final var fluidState = FluidUtils.getFluidState(world, pos);

        if (ModCompat.Fluidlogged.isFluidloggable(state)) {
            return fluidState.isEmpty() ? 0 :
                FluidProperty.timeof(fluidState) == 0 ? -1 : fluidState.getAmount();
        }

        if (FluidUtils.isWater(fluidState)) {
            return FluidProperty.timeof(fluidState) == 0 ? -1 : fluidState.getAmount();
        }

        else {
            return -1;
        }
    }
}
