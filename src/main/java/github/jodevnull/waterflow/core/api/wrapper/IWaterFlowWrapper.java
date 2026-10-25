package github.jodevnull.waterflow.core.api.wrapper;

import github.jodevnull.waterflow.core.api.IWaterFlow;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This should be only used to implement {@link IWaterFlow} for blocks using mixins.
 */
public interface IWaterFlowWrapper extends IWaterFlow
{
    float wf_fluidOffset(BlockState state);
    byte wf_getFlowIn(BlockState state);
    byte wf_getFlowOut(BlockState state);
    int wf_getVolume(BlockState state);

    @Override
    default int getVolume(BlockState state) {
        return wf_getVolume(state);
    }

    @Override
    default float fluidOffset(BlockState state) {
        return wf_fluidOffset(state);
    }

    @Override
    default byte blockFlowIn(BlockState state) {
        return wf_getFlowIn(state);
    }

    @Override
    default byte blockFlowOut(BlockState state) {
        return wf_getFlowOut(state);
    }
}
