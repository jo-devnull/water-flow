package github.jodevnull.waterflow.block;

import github.jodevnull.waterflow.compat.ModCompat;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public interface IWaterChannel
{
    static Direction facing(BlockState state) {
        return state.getValue(HORIZONTAL_FACING);
    }

    static boolean isChannel(BlockGetter getter, BlockPos pos) {
        final BlockState state = getter.getBlockState(pos);
        return (state.getBlock() instanceof IWaterChannel);
    }

    static boolean isChannel(BlockState state) {
        return state.getBlock() instanceof IWaterChannel;
    }

    static void flowNaturalWaterAbove(Level pLevel, BlockPos pos, BlockState state, FluidState fluid) {
        final Direction dir = IWaterChannel.facing(state);
        final BlockState next = pLevel.getBlockState(pos.relative(dir));

        if (next.isAir() || ModCompat.Fluidlogged.isFluidloggable(next)) {
            pLevel.setBlockAndUpdate(pos.relative(dir), fluid.createLegacyBlock());
        }
    }

    static void flowWaterAbove(WaterSystem system, BlockState state, BlockPos pos, int level) {
        final var dir = state.getValue(HORIZONTAL_FACING);
        final int nextLevel = system.getWaterLevel(pos.relative(dir));

        if (level <= 0)
            return;

        final BlockState nextState = system.getSections().getBlockState(pos.relative(dir).below());

        if (nextState.getBlock() instanceof ChannelBlock) {
            if (nextState.getValue(HORIZONTAL_FACING) == dir.getOpposite()) {
                return;
            }
        }

        if (level > 1 && level >= (nextLevel + 1) && (nextLevel >= 0 && nextLevel < 8)) {
            system.setWater(pos, level - 1);
            system.setWater(pos.relative(dir), nextLevel + 1);
        }

        else if (level == 1 && nextLevel == 0 && searchDownHillPath(system, state, pos)) {
            system.setWater(pos, 0);
            system.setWater(pos.relative(dir), 1);
        }
    }

    static boolean searchDownHillPath(WaterSystem system, BlockState state, BlockPos pos) {
        final BlockPos.MutableBlockPos above = pos.mutable();
        final BlockPos.MutableBlockPos below = pos.mutable().move(Direction.DOWN);

        int steps = 0;
        Direction dir = state.getValue(HORIZONTAL_FACING);

        while (steps < 3) {
            above.move(dir);
            below.move(dir);
            state = system.getSections().getBlockState(below);

            if (system.canFlow(above, dir)) {
                if (system.isNotFull(below)) {
                    return true;
                } else if (isChannel(state)) {
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
