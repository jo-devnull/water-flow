package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.flow.IFlowHandler;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class)
public class DoorBlockMixin implements IFlowHandler
{
    @Override
    public int wf_height(BlockState state) {
        return 0;
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        final var open = state.getValue(DoorBlock.OPEN);
        final var facing = state.getValue(DoorBlock.FACING);

        if (open) {
            return FlowHandler.get(facing.getCounterClockWise());
        } else {
            return FlowHandler.get(facing);
        }
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        final var open = state.getValue(DoorBlock.OPEN);
        final var facing = state.getValue(DoorBlock.FACING);

        if (open) {
            return FlowHandler.get(facing.getCounterClockWise());
        } else {
            return FlowHandler.get(facing.getOpposite());
        }
    }
}
