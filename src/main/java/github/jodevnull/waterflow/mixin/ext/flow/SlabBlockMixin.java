package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.ext.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SlabBlock.class)
public class SlabBlockMixin implements IFlowHandler
{
    @Override
    public int wf$height(BlockState state) {
        return 5;
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        return dir == Direction.DOWN || dir == Direction.UP;
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        return dir == Direction.DOWN || dir == Direction.UP;
    }
}
