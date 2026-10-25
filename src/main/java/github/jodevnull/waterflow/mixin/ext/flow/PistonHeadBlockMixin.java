package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.api.wrapper.IWaterFlowWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin implements IWaterFlowWrapper
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

    @Override
    public int wf_getVolume(BlockState state) {
        return 0;
    }
}
