package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.ext.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin implements IFlowHandler
{
    @Override
    public int wf$height(BlockState state) {
        return state.getValue(DirectionalBlock.FACING) == Direction.DOWN ? 2 : 0;
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        return dir == state.getValue(DirectionalBlock.FACING);
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        return dir == state.getValue(DirectionalBlock.FACING).getOpposite();
    }
}
