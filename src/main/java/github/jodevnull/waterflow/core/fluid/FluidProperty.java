package github.jodevnull.waterflow.core.fluid;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;

public final class FluidProperty
{
    public static final int MAX_TIME = 4;
    public static final IntegerProperty TIME = IntegerProperty.create("time", 0, MAX_TIME);

    public static int timeof(FluidState state) {
        return state.getOptionalValue(TIME).orElse(0);
    }

    public static int timeof(BlockState state) {
        return state.getOptionalValue(TIME).orElse(0);
    }

    public static boolean isPersistent(FluidState state) {
        return timeof(state) == 0 || state.getAmount() == 8;
    }

    public static boolean isFinite(FluidState state) {
        return timeof(state) > 0;
    }

    public static boolean isVolatile(FluidState state) {
        return !isPersistent(state);
    }

    public static FluidState getFluidState(BlockState liquidBlock) {
        assert liquidBlock.getBlock() instanceof LiquidBlock : "Expected a liquid block!";
        return liquidBlock.getFluidState().trySetValue(TIME, liquidBlock.getValue(TIME));
    }

    public static BlockState getBlockState(FluidState fluidState) {
        if (fluidState.isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }

        assert fluidState.hasProperty(TIME) : "Fluid '%s' doesn't have property TIME".formatted(fluidState.getType());
        return fluidState.createLegacyBlock().setValue(TIME, fluidState.getValue(TIME));
    }
}
