package github.jodevnull.waterflow.mixin.flow;

import github.jodevnull.waterflow.core.utils.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin implements IFlowHandler
{
    @Override
    public int wf$height(BlockState state) {
        return state.getValue(TrapDoorBlock.OPEN) ? 0 : 1;
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        final var open = state.getValue(TrapDoorBlock.OPEN);
        final var facing = state.getValue(TrapDoorBlock.FACING);

        if (dir == Direction.DOWN && !open) {
            return true;
        }

        return open && facing == dir;
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        if (dir == Direction.DOWN) {
            return false;
        }

        return wf$blockFlowIn(state, dir.getOpposite());
    }
}
