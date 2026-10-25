package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.api.wrapper.IWaterFlowWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin implements IWaterFlowWrapper
{
    @Override
    public int wf_getVolume(BlockState state) {
        return state.getValue(TrapDoorBlock.OPEN) ? 0 : 2;
    }

    @Override
    public float wf_fluidOffset(BlockState state) {
        return wf_getVolume(state) / 9.0f;
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
