package github.jodevnull.waterflow.extension.waterlogging.interfaces;

import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import net.minecraft.world.level.material.FluidState;

public interface WL$LevelChunkSection
{
    void wf_createWaterlogMap();

    Short2ByteMap wf_getWaterLogMap();

    FluidState wf_setWaterState(int x, int y, int z, FluidState state);
    FluidState wf_getWaterState(int x, int y, int z);
}
