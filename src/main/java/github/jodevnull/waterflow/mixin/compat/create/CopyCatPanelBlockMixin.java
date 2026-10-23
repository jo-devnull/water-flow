package github.jodevnull.waterflow.mixin.compat.create;

import com.simibubi.create.content.decoration.copycat.CopycatPanelBlock;
import github.jodevnull.waterflow.core.flow.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CopycatPanelBlock.class)
public class CopyCatPanelBlockMixin implements IFlowHandler
{
    @Override
    public int wf_height(BlockState state) {
        return state.getValue(CopycatPanelBlock.FACING) == Direction.DOWN ? 2 : 0;
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        return 0;
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        return 0;
    }
}
