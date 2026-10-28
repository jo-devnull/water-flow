package github.jodevnull.waterflow.block;

import github.jodevnull.waterflow.core.WaterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public interface IWaterChannel
{
    static Direction facing(BlockState state) {
        return state.getValue(HORIZONTAL_FACING);
    }

    static boolean isChannel(BlockGetter getter, BlockPos pos) {
        final BlockState state = getter.getBlockState(pos);
        return (state.getBlock() instanceof IWaterChannel);// && state.getValue(TIME) == 0;
    }

    static boolean isChannel(BlockState state) {
        return state.getBlock() instanceof IWaterChannel;
    }

    static void flowWaterAbove(WaterManager manager, BlockState state, BlockPos pos, int level) {
        final var dir = state.getValue(HORIZONTAL_FACING);
        final int nextLevel = manager.getWaterLevel(pos.relative(dir));

        if (level <= 0)
            return;

        final BlockState nextState = manager.getBlockState(pos.relative(dir).below());

        if (nextState.getBlock() instanceof WaterChannelBlock) {
            if (nextState.getValue(HORIZONTAL_FACING) == dir.getOpposite()) {
                return;
            }
        }

        if (level > 1 && level >= (nextLevel + 1) && (nextLevel >= 0 && nextLevel < 8)) {
            manager.setWater(pos, level - 1);
            manager.setWater(pos.relative(dir), nextLevel + 1);
        }

        else if (level == 1 && nextLevel == 0 && searchDownHillPath(manager, state, pos)) {
            manager.setWater(pos, 0);
            manager.setWater(pos.relative(dir), 1);
        }
    }

    static boolean searchDownHillPath(WaterManager manager, BlockState state, BlockPos pos) {
        final BlockPos.MutableBlockPos above = pos.mutable();
        final BlockPos.MutableBlockPos below = pos.mutable().move(Direction.DOWN);

        int steps = 0;
        Direction dir = state.getValue(HORIZONTAL_FACING);

        while (steps < 3) {
            above.move(dir);
            below.move(dir);
            state = manager.getBlockState(below);

            if (manager.canFlow(above, dir)) {
                if (manager.isNotFull(below)) {
                    return true;
                } else if (state.getBlock() instanceof WaterChannelBlock) {
                    dir = state.getValue(HORIZONTAL_FACING);
                } else {
                    break;
                }
            } else {
                break;
            }

            steps++;
        }

        return false;
    }
}
