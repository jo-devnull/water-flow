package github.jodevnull.waterflow.compat.create;

import com.simibubi.create.AllBlocks;
import net.minecraft.world.level.block.state.BlockState;

public class CreateImpl implements CreateCompat
{
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean isWheelPart(BlockState state) {
        return state.is(AllBlocks.WATER_WHEEL_STRUCTURAL.get())
            || state.is(AllBlocks.WATER_WHEEL.get())
            || state.is(AllBlocks.LARGE_WATER_WHEEL.get());
    }
}
