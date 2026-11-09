package github.jodevnull.waterflow.compat.create;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
