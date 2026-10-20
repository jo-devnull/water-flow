package github.jodevnull.waterflow.core.ext;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IFlowHandler
{
    int wf$height(BlockState state);

    boolean wf$blockFlowIn(BlockState state, Direction dir);
    boolean wf$blockFlowOut(BlockState state, Direction dir);
}
