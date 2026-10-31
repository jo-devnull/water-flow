package github.jodevnull.waterflow.mixin.block.flow_impl;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.api.block.wrapper.IWaterFlowBlockWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin implements IWaterFlowBlockWrapper
{
    @Override
    public float wf_fluidOffset(BlockState state) {
        return state.getValue(DirectionalBlock.FACING) == Direction.DOWN ? 0.2222f : 0.0f;
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        return FlowHandler.get(state.getValue(DirectionalBlock.FACING).getOpposite());
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        return FlowHandler.get(state.getValue(DirectionalBlock.FACING));
    }
}
