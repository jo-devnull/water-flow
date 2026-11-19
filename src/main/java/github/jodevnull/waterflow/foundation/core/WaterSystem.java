package github.jodevnull.waterflow.foundation.core;

import github.jodevnull.waterflow.foundation.compat.ModCompat;
import github.jodevnull.waterflow.foundation.config.Config;
import github.jodevnull.waterflow.foundation.core.api.IWaterSystem;
import github.jodevnull.waterflow.foundation.core.feat.EqualizeFeature;
import github.jodevnull.waterflow.foundation.core.flow.FlowHandler;
import github.jodevnull.waterflow.extension.flowmap.FlowMap;
import github.jodevnull.waterflow.foundation.core.fluid.FluidProperty;
import github.jodevnull.waterflow.foundation.core.fluid.FluidUtils;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class WaterSystem implements IWaterSystem
{
    public final ServerLevel world;
    public final FlowMap flowMap;
    public final SectionCache sections;

    private final Long2ByteMap cachedWater = new Long2ByteOpenHashMap();
    private final Long2ByteMap queuedWater = new Long2ByteOpenHashMap();
    private final Long2ShortMap cachedFlowState = new Long2ShortOpenHashMap();
    private final Long2ObjectMap<FluidState> updateQueue = new Long2ObjectOpenHashMap<>();
    private final WaterAction.Context context = new WaterAction.Context();

    public WaterSystem(ServerLevel world) {
        this.world = world;
        this.sections = new SectionCache(world);
        this.flowMap = new FlowMap(this.sections);
        queuedWater.defaultReturnValue((byte) -1);
    }

    @Override
    public ServerLevel getWorld() {
        return world;
    }

    @Override
    public SectionCache getSections() {
        return sections;
    }

    @Override
    public FlowMap getFlowMap() {
        return flowMap;
    }

    @Override
    public boolean isEmpty(BlockPos pos) {
        return getWaterLevel(pos) == 0;
    }

    @Override
    public Direction getFlow(BlockPos pos) {
        return this.flowMap.getFlow(pos);
    }

    @Override
    public void setFlow(BlockPos pos, @Nullable Direction flow) {
        this.flowMap.setFlow(pos, flow);
    }

    @Override
    public int getWaterLevel(BlockPos pos) {
        return cachedWater.computeIfAbsent(pos.asLong(), this::getWaterLevelOf);
    }

    @Override
    public short getFlowState(BlockPos pos) {
        return cachedFlowState.computeIfAbsent(pos.asLong(), lpos ->
            FlowHandler.getFlowState(sections.getBlockState(BlockPos.of(lpos)))
        );
    }

    @Override
    public boolean canFlow(BlockPos center, Direction dir) {
        return ModCompat.Fluidlogged.canFlow(this, center, dir);
    }

    @Override
    public boolean isNotFull(BlockPos pos) {
        final int level = getWaterLevel(pos);
        return level >= 0 && level < 8;
    }

    @Override
    public boolean isFull(BlockPos pos) {
        return !isNotFull(pos);
    }

    @Override
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

    @Override
    public void setWater(BlockPos pos, int level) {
        int prev = getWaterLevel(pos);

        if ((prev == 8 && level < 8) || (prev < 8 && level == 8)) {
            flowMap.updateFlowAt(pos);
        }

        cachedWater.put(pos.asLong(), (byte) level);
        queuedWater.put(pos.asLong(), (byte) level);
    }

    @Override
    public void tickWater(BlockPos pos) {
        if (pos.getY() == world.getMinBuildHeight() + 1) {
            setWater(pos, 0); return;
        }

        final int level = getWaterLevel(pos);

        if (level <= 0) {
            return;
        }

        context.setPos(pos);
        context.setLevel(level);
        context.setSystem(this);
        context.setBelow(sections.getBlockState(pos.below()));

        for (final WaterAction action : WaterAction.values()) {
            if (action.dispatch(context)) {
                return;
            }
        }

        EqualizeFeature.execute(this, pos, level);
    }

    @Override
    public void beforeTick() {
        assert cachedWater.isEmpty();
    }

    @Override
    public void afterTick() {
        cachedWater.clear();
        updateNeighbors();
        updateFluids();
        flowMap.tick();
        clear();
    }

    @Override
    public void clear() {
        sections.release();
        flowMap.clear();
        queuedWater.clear();
        updateQueue.clear();
        cachedFlowState.clear();
    }

    @Override
    public void unload() {
        cachedWater.clear();
        clear();
    }

    public void cacheFlowState(BlockPos pos, BlockState state) {
        final long key = pos.asLong();

        if (!cachedFlowState.containsKey(key)) {
            cachedFlowState.put(key, FlowHandler.getFlowState(state));
        }
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

    private boolean setWaterLevel(BlockPos pos, int level) {
        if (level < 0 || level > 8)
            return false;

        BlockState state = sections.getBlockState(pos);
        final var fluid = FluidUtils.getWater(level);

        if (ModCompat.Fluidlogged.isFluidloggable(state)) {
            ModCompat.Fluidlogged.setWater(this, state, pos, fluid);
        }

        else if (level > 0 && Config.isWaterBreakable(state)) {
            if (!state.is(Blocks.SNOW)) {
                world.destroyBlock(pos, true);
            }

            sections.setBlockState(pos, FluidProperty.getBlockState(fluid));
        }

        else if (state.isAir() || state.is(Blocks.WATER)) {
            sections.setBlockState(pos, FluidProperty.getBlockState(fluid));
        }

        if (level > 1) {
            flowMap.removeFlowAt(pos);
        }

        scheduleUpdate(pos, fluid);
        return true;
    }

    private byte getWaterLevelOf(long lpos) {
        final var pos = BlockPos.of(lpos);
        final var state = sections.getBlockState(pos);

        if (ModCompat.Fluidlogged.isLoaded()) {
            cacheFlowState(pos, state);
        }

        return (byte) FluidUtils.getWaterLevelOf(sections, pos, state);
    }

    public void update(BlockPos neighbor) {
        final var state = FluidUtils.getFluidState(sections, neighbor);

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

            if (world.isLoaded(pos)) {
                world.scheduleTick(pos, state.getType(), state.getType().getTickDelay(world));
            }
        }
    }

    public void scheduleUpdate(BlockPos pos, FluidState fluid) {
        if (fluid.isEmpty()) {
            updateQueue.remove(pos.asLong());
        } else {
            updateQueue.put(pos.asLong(), fluid);
        }
    }
}
