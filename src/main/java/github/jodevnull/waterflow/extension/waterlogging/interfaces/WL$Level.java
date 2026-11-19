package github.jodevnull.waterflow.extension.waterlogging.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface WL$Level
{
    boolean wf_setWater(BlockPos pos, FluidState water, int flags, int maxUpdateDepth);

    default boolean wf_setWater(BlockPos pos, FluidState water, int flags) {
        return WL$Level.this.wf_setWater(pos, water, flags, 512);
    }

    boolean wf_setBlockAndInsertWaterIfPossible(BlockPos pos, BlockState blockState, int flags);

    default void wf_sendWaterUpdated(BlockPos pos, int flags) {}
    default void wf_setBlocksDirty(int x1, int y1, int z1, int x2, int y2, int z2) {}
}
