package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.flow.IFlowHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements IFlowHandler
{
    @Override
    public int wf_height(BlockState state) {
        return 0;
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        return FlowHandler.all();
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        return FlowHandler.all();
    }
}
