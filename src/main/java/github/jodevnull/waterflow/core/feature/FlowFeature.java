package github.jodevnull.waterflow.core.feature;

import github.jodevnull.waterflow.core.water.WaterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Arrays;

/**
 * Adapted from: https://github.com/badgermemelord/Dihydrogen-Monoxide-Reloaded/
 * Credits to Sir Washington on github
 */
public final class FlowFeature
{
    static final int[] levels = new int[4];
    static final boolean[] flow = new boolean[4];
    static final BlockPos[] blocks = new BlockPos[4];

    public static void execute(WaterManager system, BlockPos center, int level) {
        Arrays.fill(levels, 0);

        int index = 0, originalLevel = level;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            flow[index] = system.canFlow(center, dir);
            blocks[index] = center.relative(dir);
            levels[index] = system.getWaterLevel(blocks[index]);
            index++;
        }

        int count = 0;
        int internalLevel;

        while (count < 4) {
            for (int i = 0; i < 4; i++) {
                internalLevel = flow[i] ? levels[i] : -1;
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
            if (flow[i]) {
                system.setWater(blocks[i], levels[i]);
            }
        }

        if (originalLevel != level) {
            system.setWater(center, level);
        }
    }
}
