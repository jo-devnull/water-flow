package github.jodevnull.waterflow.core.api.block.wrapper;

import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.api.block.IWaterFlowBlock;
import github.jodevnull.waterflow.core.flow.FlowHandler;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This should be only used to implement {@link IWaterFlowBlock} for blocks using mixins.
 */
public interface IWaterFlowBlockWrapper extends IWaterFlowBlock
{
    float wf_fluidOffset(BlockState state);
    byte wf_getFlowIn(BlockState state);
    byte wf_getFlowOut(BlockState state);

    @Override
    default float fluidOffset(BlockState state) {
        return wf_fluidOffset(state);
    }

    @Override
    default byte blockFlowIn(BlockState state) {
        if (Config.alwaysFlowsThrough(state)) {
            return FlowHandler.none();
        }

        return wf_getFlowIn(state);
    }

    @Override
    default byte blockFlowOut(BlockState state) {
        if (Config.alwaysFlowsThrough(state)) {
            return FlowHandler.none();
        }

        return wf_getFlowOut(state);
    }
}
