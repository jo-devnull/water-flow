package github.jodevnull.waterflow.mixin.block.flow_impl;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.api.wrapper.IWaterFlowWrapper;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StairBlock.class)
public class StairBlockMixin implements IWaterFlowWrapper
{
    @Override
    public float wf_fluidOffset(BlockState state) {
        return 0.4444444444444444444f;
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        return FlowHandler.get(state.getValue(StairBlock.FACING).getOpposite());
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        return FlowHandler.get(state.getValue(StairBlock.FACING));
    }
}
