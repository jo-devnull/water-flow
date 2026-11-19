package github.jodevnull.waterflow.extension.waterlogging.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;

public interface WL$LevelChunk
{
    FluidState wf_setWaterState(BlockPos pos, FluidState water);
}
