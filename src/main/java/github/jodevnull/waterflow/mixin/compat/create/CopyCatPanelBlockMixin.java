package github.jodevnull.waterflow.mixin.compat.create;

import com.simibubi.create.content.decoration.copycat.CopycatPanelBlock;
import github.jodevnull.waterflow.core.api.block.wrapper.IWaterFlowBlockWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CopycatPanelBlock.class)
public class CopyCatPanelBlockMixin implements IWaterFlowBlockWrapper
{
    @Override
    public float wf_fluidOffset(BlockState state) {
        return state.getValue(CopycatPanelBlock.FACING) == Direction.DOWN ? 0.2222222f : 0.0f;
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
