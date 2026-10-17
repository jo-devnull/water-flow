package github.jodevnull.waterflow.core.water;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.utils.SectionCache;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class WaterManager extends SectionCache
{
    private final WaterData data;

    public WaterManager(Level world) {
        super(world);
        this.data = new WaterData(world);
    }

    public int getWaterLevel(BlockPos pos) {
        return data.cachedWater.computeIfAbsent(pos.asLong(), this::cacheWaterLevel);
    }

    public boolean isNotFull(BlockPos pos) {
        int level = getWaterLevel(pos);
        return level >= 0 && level < 8;
    }

    public boolean isFull(BlockPos pos) {
        return !isNotFull(pos);
    }

    public void addWater(BlockPos pos, int level) {
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

    public void setWater(BlockPos pos, int level) {
        data.cachedWater.put(pos.asLong(), (byte) level);
        data.queuedWater.put(pos.asLong(), (byte) level);
    }

    private void setWaterLevel(BlockPos pos, int level) {
        if (level < 0 || level > 8)
            return;

        final var prev = getBlockState(pos);
        final var fluid = FluidUtils.getWater(level);

        if (prev.isAir() || prev.is(Blocks.WATER)) {
            setBlockState(pos, FluidProperty.getBlockState(fluid));
        }

        if (!fluid.isEmpty()) {
            data.updateQueue.put(pos.asLong(), fluid);
        }
    }

    private int getWaterLevelOf(BlockPos pos) {
        final var blockState = getBlockState(pos);

        if (blockState.isAir()) {
            return 0;
        }

        final var fluidState = FluidUtils.getFluidState(this, pos);

        if (FluidUtils.isWater(fluidState)) {
            return FluidProperty.timeof(fluidState) == 0 ? -2 : fluidState.getAmount();
        } else {
            return -1;
        }
    }

    private byte cacheWaterLevel(long lpos) {
        return (byte) getWaterLevelOf(BlockPos.of(lpos));
    }

    public void beforeTick() {
        assert data.cachedWater.isEmpty();
    }

    public void afterTick() {
        data.cachedWater.clear();
        updateNeighbors();
        updateFluids();
        clearCache();
    }

    private void update(BlockPos neighbor) {
        final var state = FluidUtils.getFluidState(this, neighbor);

        if (FluidUtils.isFiniteWater(state)) {
            data.updateQueue.put(neighbor.asLong(), state);
        }
    }

    private void updateNeighbors() {
        for (final var entry : data.queuedWater.long2ByteEntrySet()) {
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
        final Level world = data.world;

        for (final var entry : data.updateQueue.long2ObjectEntrySet()) {
            var pos = BlockPos.of(entry.getLongKey());
            var state = entry.getValue();
            world.scheduleTick(pos, state.getType(), state.getType().getTickDelay(world));
        }
    }

    private void clearCache() {
        releaseSections();
        data.queuedWater.clear();
        data.updateQueue.clear();
    }

    public void unload() {
        data.cachedWater.clear();
        clearCache();
    }
}
