package github.jodevnull.waterflow.mixin.block.flow_impl;

import github.jodevnull.waterflow.core.api.block.wrapper.IWaterFlowBlockWrapper;
import github.jodevnull.waterflow.core.flow.FlowHandler;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class)
public class DoorBlockMixin implements IWaterFlowBlockWrapper
{
    @Override
    public float wf_fluidOffset(BlockState state) {
        return 0.0f;
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
