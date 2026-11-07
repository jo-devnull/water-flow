package github.jodevnull.waterflow.core.feat;

import github.jodevnull.waterflow.core.WaterManager;
import github.jodevnull.waterflow.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Arrays;

/**
 * Original code: https://github.com/badgermemelord/Dihydrogen-Monoxide-Reloaded/
 */
public final class FlowFeature
{
    static int counter = 0;

    static final int[] levels = new int[4];
    static final boolean[] flow = new boolean[4];
    static final BlockPos.MutableBlockPos[] blocks = new BlockPos.MutableBlockPos[4];

    static {
        // maybe i'm too paranoid to preallocate everything
        for (int i = 0; i < 4; i++) {
            blocks[i] = new BlockPos.MutableBlockPos();
        }
    }

    public static void execute(WaterManager system, BlockPos center, int level) {
        Arrays.fill(levels, 0);

        final int originalLevel = level;
        for (final var dir : Direction.Plane.HORIZONTAL) {
            int j = (counter++) % 4;
            blocks [j] = blocks[j].setWithOffset(center, dir);
            levels [j] = system.getWaterLevel(blocks[j]);
            flow   [j] = system.canFlow(center, dir);
        }

        if (Utils.isAllFalse(flow)) {
            return;
        }

        int count = 0;
        int internalLevel;

        while (count < 4) {
            for (int i = 0; i < 4; i++) {
                internalLevel = flow[i] ? levels[i] : -1;
                if (internalLevel != -1) {
                    if (level >= (internalLevel + 1)) {
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
                system.flowMap.removeFlowAt(blocks[i]);
            }
        }

        if (originalLevel != level) {
            system.setWater(center, level);
            system.flowMap.removeFlowAt(center);
        }
    }
}
