package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.flow.IFlowHandler;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StairBlock.class)
public class StairBlockMixin implements IFlowHandler
{
    @Override
    public int wf_height(BlockState state) {
        return 4;
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
