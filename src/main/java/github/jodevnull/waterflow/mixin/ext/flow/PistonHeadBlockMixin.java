package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.flow.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin implements IFlowHandler
{
    @Override
    public int wf_height(BlockState state) {
        return state.getValue(DirectionalBlock.FACING) == Direction.DOWN ? 2 : 0;
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
