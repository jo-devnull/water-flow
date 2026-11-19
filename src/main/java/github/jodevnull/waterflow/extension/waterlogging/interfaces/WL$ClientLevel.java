package github.jodevnull.waterflow.extension.waterlogging.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;

public interface WL$ClientLevel
{
    void wf_syncWaterState(BlockPos pos, FluidState water);
}
