package github.jodevnull.waterflow.core.water;

import de.leximon.fluidlogged.Fluidlogged;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.ext.IFlowHandler;
import github.jodevnull.waterflow.core.utils.SectionCache;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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

    public int getBlockHeight(BlockPos pos) {
        return data.cachedHeight.computeIfAbsent(pos.asLong(), lpos -> {
            final BlockState state = getBlockState(BlockPos.of(lpos));

            if (state.is(WaterFlow.CAN_BLOCK_FLOW)) {
                return (byte) ((IFlowHandler) state.getBlock()).wf$height(state);
            }

            return (byte) 0;
        });
    }

    public boolean canFlow(BlockPos center, Direction dir) {
        BlockState state = getBlockState(center);

        if (state.is(WaterFlow.CAN_BLOCK_FLOW)) {
            if (((IFlowHandler) state.getBlock()).wf$blockFlowIn(state, dir)) {
                return false;
            }
        }

        state = getBlockState(center.relative(dir));

        if (state.is(WaterFlow.CAN_BLOCK_FLOW)) {
            return !((IFlowHandler) state.getBlock()).wf$blockFlowOut(state, dir);
        }

        return true;
    }

    public boolean isNotFull(BlockPos pos) {
        int level = getWaterLevel(pos) + getBlockHeight(pos);

        return level >= 0 && level < 8;
    }

    public boolean isFull(BlockPos pos) {
        return !isNotFull(pos);
    }

    public void addWater(BlockPos pos, int level) {
        final int existingWater = getWaterLevel(pos);

        if (existingWater == -1) {
            return;
        }

        int total = existingWater + level;

        if (total > 8) {
            setWater(pos, 8);
            moveWater(pos.above(), total - 8);
        } else {
            setWater(pos, total);
        }
    }


    public void moveWater(BlockPos center, int amount) {
        for (final var dir : Direction.Plane.HORIZONTAL) {
            var rPos = center.relative(dir);
            int rLevel = getWaterLevel(rPos);

            if (rLevel < 0)
                continue;

            if (canFlow(center, dir) && amount >= (rLevel + 1)) {
                amount -= 1;
                addWater(rPos, 1);
            }
        }

        addWater(center, amount);
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

        if (Fluidlogged.isFluidloggable(prev)) {
            setFluidState(pos, fluid);

            /// FIXED?: some blocks may need to be waterlogged to work, but this breaks create
            if (prev.hasProperty(BlockStateProperties.WATERLOGGED)) {
                if (level == 8 && !prev.getValue(BlockStateProperties.WATERLOGGED)) {
                    setBlockState(pos, prev.setValue(BlockStateProperties.WATERLOGGED, true));
                } else if (level < 8 && prev.getValue(BlockStateProperties.WATERLOGGED)) {
                    setBlockState(pos, prev.setValue(BlockStateProperties.WATERLOGGED, false));
                }
            }
        }

        else if (prev.isAir() || prev.is(Blocks.WATER)) {
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

        else if (Fluidlogged.isFluidloggable(blockState)) {
            final var fluidState = FluidUtils.getFluidState(this, pos);
            return fluidState.isEmpty() ? 0 : FluidProperty.timeof(fluidState) == 0 ? -1 : fluidState.getAmount();
        }

        final var fluidState = FluidUtils.getFluidState(this, pos);

        if (FluidUtils.isWater(fluidState)) {
            return FluidProperty.timeof(fluidState) == 0 ? -1 : fluidState.getAmount();
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

    public void update(BlockPos neighbor) {
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
        data.cachedHeight.clear();
        data.queuedWater.clear();
        data.updateQueue.clear();
    }

    public void unload() {
        data.cachedWater.clear();
        clearCache();
    }
}
