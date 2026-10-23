package github.jodevnull.waterflow.core.feature;

import github.jodevnull.waterflow.core.WaterManager;
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

    public static void execute(WaterManager system, BlockPos center, int level) {
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

        flood(data, radius, radius);

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

    public static void flood(int[][] data, int x, int y) {
        dfsFill(data, x, y);
    }

    public static void dfsFill(int[][] data, int i, int j) {
        if(i >= 0 && j >=0 && i < data.length && j < data[0].length && data[i][j]>= -1 && data[i][j] < 10){
            if (data[i][j] >= 0) {
                data[i][j]+=10;

                dfsFill(data, i + 1, j);
                dfsFill(data, i - 1, j);
                dfsFill(data, i, j + 1);
                dfsFill(data, i, j - 1);
            }
            else {
                data[i][j] = -2;
            }
        }
    }
}
