package github.jodevnull.waterflow.core.feature;

import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Arrays;

public final class FlowFeature
{
    static int count = 0;
    static final int[] levels = new int[4];
    static final BlockPos[] blocks = new BlockPos[4];

    public static void execute(WaterSystem system, BlockPos center, int centerLevel) {
        int level = system.getWaterLevel(center);

        Arrays.fill(levels, 0);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            final int i = count++ % 4;
            blocks[i] = center.relative(dir);
            levels[i] = system.getWaterLevel(blocks[i]);
        }

        int count = 0;
        int internalLevel;

        while (count < 4) {
            for (int i = 0; i < 4; i++) {
                internalLevel = levels[i];
                if (internalLevel != -1) {
                    if ((level >= (internalLevel + 1))) {
                        internalLevel += 1;
                        levels[i] = internalLevel;
                        level -= 1;
                    } else {
                        count += 1;
                    }
                } else {
                    count += 1;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            system.setWater(blocks[i], levels[i]);
        }

        system.setWater(center, level);
    }
}
