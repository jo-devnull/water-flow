package github.jodevnull.waterflow.mixin.ext.flow;

import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.api.wrapper.IWaterFlowWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlabBlock.class)
public class SlabBlockMixin implements IWaterFlowWrapper
{
    @Shadow
    @Final
    public static EnumProperty<SlabType> TYPE;

    @Override
    public float wf_fluidOffset(BlockState state) {
        return switch (state.getValue(TYPE)) {
            case TOP    -> .00f;
            case BOTTOM -> .55f;
            case DOUBLE -> .88f;
        };
    }

    @Override
    public byte wf_getFlowIn(BlockState state) {
        byte result = (byte) 0;

        if (state.getValue(TYPE) == SlabType.DOUBLE) {
            return FlowHandler.none();
        }

        if (state.getValue(TYPE) == SlabType.TOP) {
            // Block the flow with those directions
            result |= FlowHandler.get(Direction.DOWN);
        }

        return result;
    }

    @Override
    public byte wf_getFlowOut(BlockState state) {
        return FlowHandler.all();
    }

    @Override
    public int wf_getVolume(BlockState state) {
        return 4;
    }
}
