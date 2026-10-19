package github.jodevnull.waterflow.mixin.flow;

import github.jodevnull.waterflow.core.utils.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StairBlock.class)
public class StairBlockMixin implements IFlowHandler
{
    @Override
    public int wf$height(BlockState state) {
        return 4;
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        return dir != state.getValue(StairBlock.FACING);
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        return Direction.Plane.HORIZONTAL.test(dir);
    }
}
