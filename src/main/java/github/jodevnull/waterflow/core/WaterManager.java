package github.jodevnull.waterflow.core;

import github.jodevnull.waterflow.compat.ModCompat;
import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.flow.FlowMap;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class WaterManager extends SectionCache
{
    public final Level world;

    private final Long2ByteMap
        cachedWater = new Long2ByteOpenHashMap(),
        queuedWater = new Long2ByteOpenHashMap();

    public final FlowMap flowMap;
    private final Long2ShortMap cachedFlowState = new Long2ShortOpenHashMap();
    private final Long2ObjectMap<FluidState> updateQueue = new Long2ObjectOpenHashMap<>();

    public WaterManager(Level world) {
        super(world);

        this.world = world;
        this.flowMap = new FlowMap(this);

        queuedWater.defaultReturnValue((byte) -1);
    }

    public boolean isEmpty(BlockPos pos) {
        return getWaterLevel(pos) == 0;
    }

    public Direction getFlow(BlockPos pos) {
        return this.flowMap.getFlow(pos);
    }

    public Direction setFlow(BlockPos pos, @Nullable Direction flow) {
        return this.flowMap.setFlow(pos, flow);
    }

    public int getWaterLevel(BlockPos pos) {
        return cachedWater.computeIfAbsent(pos.asLong(), this::cacheWaterLevel);
    }

    // FIXME performance
    public short getFlowState(BlockPos pos) {
        return cachedFlowState.computeIfAbsent(pos.asLong(), lpos ->
            FlowHandler.getFlowState(getBlockState(BlockPos.of(lpos)))
        );
    }

    public void cacheFlowState(BlockPos pos, BlockState state) {
        final long key = pos.asLong();

        if (!cachedFlowState.containsKey(key)) {
            cachedFlowState.put(key, FlowHandler.getFlowState(state));
        }
    }

    // FIXME performance
    public boolean canFlow(BlockPos center, Direction dir) {
        return ModCompat.Fluidlogged.canFlow(this, center, dir);
    }

    public boolean isNotFull(BlockPos pos) {
        final int level = getWaterLevel(pos);
        return level >= 0 && level < 8;
    }

    public boolean isFull(BlockPos pos) {
        return !isNotFull(pos);
    }

    public void addWater(BlockPos pos, int level, Direction flow) {
        final int existingWater = getWaterLevel(pos);

        if (existingWater == -1) {
            return;
        }

        int total = existingWater + level;

        if (total > 8) {
            setWater(pos, 8);

            if (getWaterLevel(pos.relative(flow)) > -1) {
                moveWater(pos.relative(flow), total - 8, flow);
            } else if (flow != Direction.UP) {
                moveWater(pos.above(), total - 8, Direction.UP);
            }

        } else {
            setWater(pos, total);
        }
    }

    public void addWater(BlockPos pos, int level) {
        addWater(pos, level, Direction.UP);
    }

    public void moveWater(BlockPos center, int amount, Direction flow) {
        for (final var dir : Direction.Plane.HORIZONTAL) {
            var rPos = center.relative(dir);
            int rLevel = getWaterLevel(rPos);

            if (rLevel < 0)
                continue;

            if (canFlow(center, dir) && amount >= (rLevel + 1)) {
                amount -= 1;
                addWater(rPos, 1, dir);
            }
        }

        addWater(center, amount, flow);
    }

    public void setWater(BlockPos pos, int level) {
        int prev = getWaterLevel(pos);

        if ((prev == 8 && level < 8) || (prev < 8 && level == 8)) {
            flowMap.updateFlowAt(pos);
        }

        cachedWater.put(pos.asLong(), (byte) level);
        queuedWater.put(pos.asLong(), (byte) level);
    }

    public void scheduleUpdate(BlockPos pos, FluidState fluid) {
        if (fluid.isEmpty()) {
            updateQueue.remove(pos.asLong());
        } else {
            updateQueue.put(pos.asLong(), fluid);
        }
    }

    private boolean setWaterLevel(BlockPos pos, int level) {
        if (level < 0 || level > 8)
            return false;

        BlockState state = getBlockState(pos);
        final var fluid = FluidUtils.getWater(level);

        if (ModCompat.Fluidlogged.isFluidloggable(state)) {
            ModCompat.Fluidlogged.setWater(this, state, pos, fluid);
        }

        else if (state.isAir() || state.is(Blocks.WATER)) {
            setBlockState(pos, FluidProperty.getBlockState(fluid));
        }

        if (level > 1) {
            flowMap.removeFlowAt(pos);
        }

        scheduleUpdate(pos, fluid);
        return true;
    }

    private int getWaterLevelOf(BlockPos pos) {
        final var state = getBlockState(pos);

        if (ModCompat.Fluidlogged.isLoaded()) {
            cacheFlowState(pos, state);
        }

        if (state.isAir()) {
            return 0;
        }

        final var fluidState = FluidUtils.getFluidState(this, pos);

        if (ModCompat.Fluidlogged.isFluidloggable(state)) {
            return fluidState.isEmpty() ? 0 :
                FluidProperty.timeof(fluidState) == 0 ? -1 : fluidState.getAmount();
        }

        if (FluidUtils.isWater(fluidState)) {
            return FluidProperty.timeof(fluidState) == 0 ? -1 : fluidState.getAmount();
        }

        else {
            return -1;
        }
    }

    private byte cacheWaterLevel(long lpos) {
        return (byte) getWaterLevelOf(BlockPos.of(lpos));
    }

    public void beforeTick() {
        assert cachedWater.isEmpty();
    }

    public void afterTick() {
        cachedWater.clear();
        updateNeighbors();
        updateFluids();
        flowMap.tick();
        clearCache();
    }

    public void update(BlockPos neighbor) {
        final var state = FluidUtils.getFluidState(this, neighbor);

        if (FluidUtils.isFiniteWater(state)) {
            updateQueue.put(neighbor.asLong(), state);
        }
    }

    private void updateNeighbors() {
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (final var entry : queuedWater.long2ByteEntrySet()) {
            pos.set(entry.getLongKey());

            if (!world.isLoaded(pos)) {
                continue;
            }

            if (setWaterLevel(pos, entry.getByteValue())) {
                update(pos.west());
                update(pos.east());
                update(pos.below());
                update(pos.above());
                update(pos.north());
                update(pos.south());
            }
        }
    }

    private void updateFluids() {
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (final var entry : updateQueue.long2ObjectEntrySet()) {
            final var state = entry.getValue();

            pos.set(entry.getLongKey());

            // if (flowMap.getFlow(pos) != Direction.UP) {
                world.scheduleTick(pos, state.getType(), state.getType().getTickDelay(world));
            // }
        }
    }

    private void clearCache() {
        releaseSections();
        flowMap.clear();
        queuedWater.clear();
        updateQueue.clear();
        cachedFlowState.clear();
    }

    public void unload() {
        cachedWater.clear();
        flowMap.clear();
        clearCache();
    }
}
