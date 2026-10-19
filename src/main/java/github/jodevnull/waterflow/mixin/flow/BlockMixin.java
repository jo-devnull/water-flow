package github.jodevnull.waterflow.mixin.flow;

import github.jodevnull.waterflow.core.utils.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements IFlowHandler
{
    @Override
    public int wf$height(BlockState state) {
        return 0;
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        return false;
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        return false;
    }
}
