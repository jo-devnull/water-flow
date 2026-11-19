package github.jodevnull.waterflow.foundation.compat.create;

import net.minecraft.world.level.block.state.BlockState;

public interface CreateCompat
{
    default boolean isLoaded() {
        return false;
    }

    default boolean isWheelPart(BlockState state) {
        return false;
    }
}
