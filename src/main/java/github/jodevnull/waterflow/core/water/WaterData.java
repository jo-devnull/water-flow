package github.jodevnull.waterflow.core.water;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;

/**
 * All data related to the water flowing system is stored here. We separate data from logic.
 * The logic is defined in a system interface. The system queries the data to act on it.
 */
public class WaterData
{
    public final Level world;
    public final Long2ByteMap cachedWater = new Long2ByteOpenHashMap();
    public final Long2ByteMap queuedWater = new Long2ByteOpenHashMap();
    public final Long2ByteMap movingWater = new Long2ByteOpenHashMap();
    public final Long2ObjectMap<FluidState> updateQueue = new Long2ObjectOpenHashMap<>();
    public final Long2ObjectMap<LevelChunkSection> sections = new Long2ObjectOpenHashMap<>();

    public WaterData(Level world) {
        this.world = world;
    }
}
