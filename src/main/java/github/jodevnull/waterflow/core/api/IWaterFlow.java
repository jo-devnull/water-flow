package github.jodevnull.waterflow.core.api;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import github.jodevnull.waterflow.core.flow.FlowHandler;

public interface IWaterFlow
{
    /**
     * Some blocks have a volume. For example, a single slab has half the volume of a full block.
     * This is used to tell the flow algorithm how much water can be inside a non-full block.
     *
     * @param state The block state of {@code this} block.
     * @return The block volume. Must be in the range {@code (0, 8)}
     */
    int getVolume(BlockState state);

    /**
     * Use this method to return a combination of directions that should block
     * the flow of water to inside of {@code this} block. For example, a trapdoor
     * will not let water flow down a hole if it's not open.
     * <p>
     * Use {@link FlowHandler#get(Direction)} to combine multiple directions.
     *
     * @param state The block state of {@code this} block.
     * @return a bitmask of the blocked directions.
     */
    byte blockFlowIn(BlockState state);

    /**
     * Use this method to return a combination of directions that should block
     * the flow of water to outside of {@code this} block. For example, a
     * trapdoor will not let water flow down itself if it's not open.
     * <p>
     * Use {@link FlowHandler#get(Direction)} to combine multiple directions.
     *
     * @param state The block state of {@code this} block.
     * @return a bitmask of the blocked directions.
     */
    byte blockFlowOut(BlockState state);

    /**
     * Some blocks may need to offset water vertically (if they have a volume),
     * otherwise water will get rendered (but visually hidden) inside of it.
     *
     * @param state The block state of {@code this} block.
     * @return the Y offset to render the fluid.
     */
    float fluidOffset(BlockState state);
}
