package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.ext.IFlowHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlabBlock.class)
public class SlabBlockMixin implements IFlowHandler
{
    @Shadow
    @Final
    public static EnumProperty<SlabType> TYPE;

    @Override
    public int wf$height(BlockState state) {
        return switch (state.getValue(TYPE)) {
            case TOP    -> 0;
            case BOTTOM -> 5;
            case DOUBLE -> 8;
        };
    }

    @Override
    public boolean wf$blockFlowIn(BlockState state, Direction dir) {
        if (state.getValue(TYPE) == SlabType.BOTTOM) {
            return dir == Direction.DOWN || dir == Direction.UP;
        }

        return false;
    }

    @Override
    public boolean wf$blockFlowOut(BlockState state, Direction dir) {
        if (state.getValue(TYPE) == SlabType.DOUBLE) {
            return true;
        }

        if (state.getValue(TYPE) == SlabType.TOP) {
            return dir == Direction.DOWN || dir == Direction.UP;
        }

        return false;
    }
}
