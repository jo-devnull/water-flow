package github.jodevnull.waterflow.mixin.block.flow_impl;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.api.block.wrapper.IWaterFlowBlockWrapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements IWaterFlowBlockWrapper
{
    @Override
    public float wf_fluidOffset(BlockState state) {
        return 0f;
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
