package github.jodevnull.waterflow.core.water;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class WaterData
{
    public final Level world;

    public final Long2ByteMap
        cachedWater = new Long2ByteOpenHashMap(),
        queuedWater = new Long2ByteOpenHashMap(),
        cachedHeight = new Long2ByteOpenHashMap();

    public final Long2ObjectMap<FluidState> updateQueue = new Long2ObjectOpenHashMap<>();

    public WaterData(Level world) {
        this.world = world;
    }
}
