package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.flow.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin implements IFlowHandler
{
    @Override
    public int wf_height(BlockState state) {
        return state.getValue(TrapDoorBlock.OPEN) ? 0 : 1;
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        final var open = state.getValue(TrapDoorBlock.OPEN);
        final var facing = state.getValue(TrapDoorBlock.FACING);

        if (open) {
            return FlowHandler.get(facing);
        } else {
            return FlowHandler.get(Direction.DOWN);
        }
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        final var open = state.getValue(TrapDoorBlock.OPEN);
        final var facing = state.getValue(TrapDoorBlock.FACING);

        if (open) {
            return FlowHandler.get(facing.getOpposite());
        } else {
            return FlowHandler.get(Direction.DOWN);
        }
    }
}
