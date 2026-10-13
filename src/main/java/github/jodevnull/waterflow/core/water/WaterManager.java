package github.jodevnull.waterflow.core.water;

import github.jodevnull.waterflow.core.utils.SectionCache;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface WaterManager extends WaterAccessor
{
    WaterData data();

    private SectionCache sections() {
        return (SectionCache) this;
    }

    default void beforeTick() {
        assert data().cachedWater.isEmpty();
    }

    default void afterTick() {
        data().cachedWater.clear();
        updateNeighbors();
        updateFluids();
        clearCache();
    }

    private void update(BlockPos neighbor) {
        final var state = FluidUtils.getFluidState(sections(), neighbor);

        if (FluidUtils.isFiniteWater(state)) {
            data().updateQueue.put(neighbor.asLong(), state);
        }
    }

    private void updateNeighbors() {
        for (final var entry : data().queuedWater.long2ByteEntrySet()) {
            var pos = BlockPos.of(entry.getLongKey());
            setWaterLevel(pos, entry.getByteValue());

            update(pos.west());
            update(pos.east());
            update(pos.below());
            update(pos.above());
            update(pos.north());
            update(pos.south());
        }
    }

    private void updateFluids() {
        final Level world = data().world;

        for (final var entry : data().updateQueue.long2ObjectEntrySet()) {
            var pos = BlockPos.of(entry.getLongKey());
            var state = entry.getValue();
            world.scheduleTick(pos, state.getType(), state.getType().getTickDelay(world));
        }
    }

    private void clearCache() {
        sections().release();
        data().queuedWater.clear();
        data().updateQueue.clear();
    }

    default void unload() {
        data().cachedWater.clear();
        clearCache();
    }
}
