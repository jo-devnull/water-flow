package github.jodevnull.waterflow.core.feature;

import github.jodevnull.waterflow.core.water.WaterSystem;
import github.jodevnull.waterflow.core.utils.FloodFill;
import net.minecraft.core.BlockPos;

import java.util.Arrays;

/**
 * Adapted from: https://github.com/badgermemelord/Dihydrogen-Monoxide-Reloaded/
 * Credits to Sir Washington on github
 */
public final class EqualizeFeature
{
    private static final int radius = 2;
    private static final int diameter = (2 * radius) + 1;
    private static final int[][] data = new int[diameter][diameter];

    public static void execute(WaterSystem system, BlockPos center, int level) {
        int minLevel = 99;

        for (final int[] array : data) {
            Arrays.fill(array, 0);
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int i = dx + radius;
                int j = dz + radius;
                data[i][j] = system.getWaterLevel(center.offset(dx, 0, dz));
            }
        }

        FloodFill.flood(data, radius, radius);

        for (int i = 0; i < diameter - 1; i++) {
            for (int j = 0; j < diameter - 1; j++) {
                if (data[i][j] >= 10 && data[i][j] < minLevel)
                    minLevel = data[i][j];
            }
        }

        int range = level + 10 - minLevel;

        if (range == 1) {
            PuddleFeature.execute(system, center, level);
        } else if (range > 1) {
            FlowFeature.execute(system, center, level);
        }
    }
}
