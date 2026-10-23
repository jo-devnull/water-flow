package github.jodevnull.waterflow.core.flow;

import net.minecraft.world.level.block.state.BlockState;

public interface IFlowHandler
{
    int wf_height(BlockState state);
    byte wf_getFlowIn(BlockState state);
    byte wf_getFlowOut(BlockState state);
}
