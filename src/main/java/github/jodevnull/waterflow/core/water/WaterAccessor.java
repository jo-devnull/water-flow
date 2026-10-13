package github.jodevnull.waterflow.core.water;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.core.utils.SectionCache;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public interface WaterAccessor
{
    WaterData data();

    private SectionCache sections() {
        return (SectionCache) this;
    }

    default int getWaterLevel(BlockPos pos) {
        return data().cachedWater.computeIfAbsent(pos.asLong(), this::cacheWaterLevel);
    }

    private byte cacheWaterLevel(long lpos) {
        return (byte) getWaterLevelOf(BlockPos.of(lpos));
    }

    private int getWaterLevelOf(BlockPos pos) {
        final var blockState = sections().getBlockState(pos);

        if (blockState.isAir()) {
            return 0;
        }

        final var fluidState = FluidUtils.getFluidState(sections(), pos);

        if (FluidUtils.isFiniteWater(fluidState)) {
            return fluidState.getAmount();
        } else {
            return -1;
        }
    }

    default void addWater(BlockPos pos, int level) {
        final var existingWater = getWaterLevel(pos);

        if (existingWater == -1) {
            WaterFlow.logger.error("Tried to add water to a full block");
            return;
        }

        int total = existingWater + level;

        // We can't add more water than this
        assert total <= 16;

        if (total > 8) {
            // Implement water displacement
            setWater(pos, 8);
            addWater(pos.above(), total - 8);
        } else {
            setWater(pos, total);
        }
    }

    default void setWater(BlockPos pos, int level) {
        data().cachedWater.put(pos.asLong(), (byte) level);
        data().queuedWater.put(pos.asLong(), (byte) level);
    }

    default void setWaterLevel(BlockPos pos, int level) {
        if (level < 0 || level > 8)
            return;

        final var prev = sections().getBlockState(pos);
        final var fluid = FluidUtils.getWater(level);

        if (prev.isAir() || prev.is(Blocks.WATER)) {
            sections().setBlockState(pos, FluidProperty.getBlockState(fluid));
        }

        if (!fluid.isEmpty()) {
            data().updateQueue.put(pos.asLong(), fluid);
        }
    }

    default boolean isNotFull(BlockPos pos) {
        int level = getWaterLevel(pos);
        return level >= 0 && level < 8;
    }

    default boolean isFull(BlockPos pos) {
        return !isNotFull(pos);
    }
}