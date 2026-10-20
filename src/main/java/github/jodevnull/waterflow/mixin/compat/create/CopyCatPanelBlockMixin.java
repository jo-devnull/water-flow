package github.jodevnull.waterflow.mixin.compat.create;

import com.simibubi.create.content.decoration.copycat.CopycatPanelBlock;
import github.jodevnull.waterflow.core.ext.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CopycatPanelBlock.class)
public class CopyCatPanelBlockMixin implements IFlowHandler
{
    @Override
    public int wf$height(BlockState state) {
        return state.getValue(CopycatPanelBlock.FACING) == Direction.DOWN ? 2 : 0;
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        final var facing = state.getValue(CopycatPanelBlock.FACING);

        if (dir == Direction.DOWN) {
            return true;
        }

        return facing == dir.getOpposite();
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        final var facing = state.getValue(CopycatPanelBlock.FACING);

        if (dir == Direction.DOWN) {
            return true;
        }

        return facing == dir;
    }
}
