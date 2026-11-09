package github.jodevnull.waterflow.compat.fluidlogged;

import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.WaterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

public interface FluidLoggedCompat
{
    default boolean isLoaded() {
        return false;
    }

    default boolean isFluidloggable(BlockState state) {
        return false;
    }

    default void setFluidState(Level level, LevelChunkSection section, BlockPos pos, FluidState fluidState, int flags) {}

    default void setFluid(Level level, BlockPos pos, FluidState fluidState, int flags, int maxUpdateDepth) {}

    default boolean setWater(WaterManager manager, BlockState state, BlockPos pos, FluidState fluid) {
        return false;
    }

    default FluidState getFluidState(LevelChunkSection section, BlockPos pos) {
        return section.getFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    default boolean canFlow(WaterManager manager, BlockPos center, Direction dir) {
        return true;
    }

    default BlockState handleBlockRemoval(Level instance, BlockPos blockPos, int flags, int maxUpdateDepth) {
        final var fluidState = instance.getFluidState(blockPos);
        return fluidState.createLegacyBlock().trySetValue(TIME, Config.getEvaporationTime());
    }
}
