package github.jodevnull.waterflow.mixin.block.flow_impl;

import github.jodevnull.waterflow.core.api.block.wrapper.IWaterFlowBlockWrapper;
import github.jodevnull.waterflow.core.flow.FlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin implements IWaterFlowBlockWrapper
{
    @Override
    public float wf_fluidOffset(BlockState state) {
        return state.getValue(TrapDoorBlock.OPEN) ? 0 : 0.2222f;
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        final var half = state.getValue(TrapDoorBlock.HALF);
        final var open = state.getValue(TrapDoorBlock.OPEN);
        final var facing = state.getValue(TrapDoorBlock.FACING);

        if (open) {
            return FlowHandler.get(facing);
        } else {
            return switch (half) {
                case TOP    -> FlowHandler.get(Direction.DOWN);
                case BOTTOM -> FlowHandler.get(Direction.UP);
            };
        }
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        final var half = state.getValue(TrapDoorBlock.HALF);
        final var open = state.getValue(TrapDoorBlock.OPEN);
        final var facing = state.getValue(TrapDoorBlock.FACING);

        if (open) {
            return FlowHandler.get(facing.getOpposite());
        } else {
            return switch (half) {
                case TOP    -> FlowHandler.get(Direction.UP);
                case BOTTOM -> FlowHandler.get(Direction.DOWN);
            };
        }
    }
}
