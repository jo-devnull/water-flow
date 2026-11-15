package github.jodevnull.waterflow.core;

import github.jodevnull.waterflow.core.api.world.IWaterSystemAccessor;
import github.jodevnull.waterflow.core.flow.FlowMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public interface IWaterSystem
{
    FlowMap getFlowMap();
    ServerLevel getWorld();
    SectionCache getSections();

    boolean isEmpty(BlockPos pos);
    boolean isNotFull(BlockPos pos);
    boolean isFull(BlockPos pos);
    boolean canFlow(BlockPos center, Direction dir);

    short getFlowState(BlockPos pos);

    Direction getFlow(BlockPos pos);
    void setFlow(BlockPos pos, Direction flow);

    int getWaterLevel(BlockPos pos);
    void setWater(BlockPos pos, int level);
    void addWater(BlockPos pos, int level, Direction flow);

    default void addWater(BlockPos pos, int level) {
        addWater(pos, level, Direction.UP);
    }

    void beforeTick();
    void afterTick();
    void unload();
    void clear();

    void tickWater(BlockPos pos);

    static Optional<IWaterSystem> get(ServerLevel world) {
        return Optional.ofNullable(IWaterSystemAccessor.get(world));
    }

    static Optional<Integer> getWaterLevel(ServerLevel level, BlockPos pos) {
        return get(level).map(manager -> manager.getWaterLevel(pos));
    }

    static void setWaterLevel(ServerLevel level, BlockPos pos, int amount) {
        get(level).ifPresent(manager -> manager.setWater(pos, amount));
    }

    static void spread(IWaterSystem water, BlockPos pos, int amount, Direction dir) {
        if (water.getWaterLevel(pos.relative(dir)) > -1) {
            water.addWater(pos.relative(dir), amount, dir);
        } else {
            water.addWater(pos.above(), amount, dir);
        }
    }

    static void spread(ServerLevel level, BlockPos pos, int amount) {
        get(level).ifPresent(system -> spread(system, pos, amount, Direction.UP));
    }
}
