package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.ext.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class)
public class DoorBlockMixin implements IFlowHandler
{
    @Override
    public int wf$height(BlockState state) {
        return 0;
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        final var open = state.getValue(DoorBlock.OPEN);
        final var facing = state.getValue(DoorBlock.FACING);

        if (open) {
            return dir == facing.getCounterClockWise();
        }

        return facing == dir.getOpposite();
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        final var open = state.getValue(DoorBlock.OPEN);
        final var facing = state.getValue(DoorBlock.FACING);

        if (open) {
            return dir == facing.getCounterClockWise();
        }

        return facing == dir;
    }
}
