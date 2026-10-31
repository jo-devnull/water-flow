package github.jodevnull.waterflow.core.feat;

import github.jodevnull.waterflow.block.IChannelBlock;
import github.jodevnull.waterflow.core.WaterManager;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.utils.PathfinderBFS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from: https://github.com/badgermemelord/Dihydrogen-Monoxide-Reloaded/
 * Credits to Sir Washington on github
 */
public final class PuddleFeature
{
    public static final int PUDDLE_RADIUS = 4;
    public static final int PUDDLE_DIAMETER = PUDDLE_RADIUS * 2 + 1;

    private static BlockPos pos;

    private static final int[][] bfsMatrix = new int[PUDDLE_DIAMETER][PUDDLE_DIAMETER];
    private static final List<PathfinderBFS.Node> holes = new ArrayList<>(8);

    private static int xX;
    private static int zZ;

    private static WaterManager system;

    public static void execute(WaterManager sys, BlockPos center, int level) {
        system = sys; pos = center;

        if (level == 1 && sys.isFull(pos.below())) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            // fill in the bfsMatrix
            xX = x - PUDDLE_RADIUS;
            zZ = z - PUDDLE_RADIUS;

            for (int iX = 0; iX < PUDDLE_DIAMETER; iX++) {
                for (int iZ = 0; iZ < PUDDLE_DIAMETER; iZ++) {
                    BlockPos internalPos = new BlockPos(iX + xX, y, iZ + zZ);
                    bfsMatrix[iX][iZ] = system.isEmpty(internalPos) ? 9 : -1;
                }
            }

            //union start
            for (int currentRadius = 1; currentRadius <= PUDDLE_RADIUS; currentRadius++) {
                int xL = x - currentRadius;
                int xR = x + currentRadius;
                int zT = z + currentRadius;
                int zB = z - currentRadius;

                holes.clear();

                testLine(xL, zT, xR, zT);
                testLine(xL, zB, xR, zB);
                testLine(xL, zB, xL, zT);
                testLine(xR, zB, xR, zT);

                if (holes.isEmpty())
                    continue;

                bfsMatrix[4][4] = -3;

                if (holeFound())
                    break;
            }
        }

        system = null;
    }

    private static boolean holeFound() {
        int[][] result = PathfinderBFS.distanceMapperBFS(bfsMatrix, holes);

        int minDistance = 255;
        Direction direction = null;

        if (result[4][3] < minDistance && result[4][3] >= 0) {
            minDistance = result[4][3];
            direction = Direction.NORTH;
        }
        if (result[3][4] < minDistance && result[3][4] >= 0) {
            minDistance = result[3][4];
            direction = Direction.WEST;
        }
        if (result[4][5] < minDistance && result[4][5] >= 0) {
            minDistance = result[4][5];
            direction = Direction.SOUTH;
        }
        if (result[5][4] < minDistance && result[5][4] >= 0) {
            minDistance = result[5][4];
            direction = Direction.EAST;
        }

        if (minDistance <= 4) {
            return move(direction);
        }

        return false;
    }

    private static boolean move(Direction dir) {
        if (system.canFlow(pos, dir)) {
            system.setWater(pos, 0);
            system.addWater(pos.relative(dir), 1);
            return true;
        }

        return false;
    }

    // its actual test rect but ssssh...
    private static final BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();
    private static final BlockPos.MutableBlockPos testPos2 = new BlockPos.MutableBlockPos();

    private static void testLine(int x, int z, int toX, int toZ) {
        for (int iX = x; iX <= toX; iX++) {
            for (int iZ = z; iZ <= toZ; iZ++) {
                int relX = iX-xX;
                int relZ = iZ-zZ;

                testPos.set(iX, pos.getY()-1, iZ);
                testPos2.set(iX, pos.getY(), iZ);
                int uLevel = system.getWaterLevel(testPos2);

                if (isHole(testPos, testPos2) && (uLevel == 0 || uLevel == 1)) {
                    holes.add(new PathfinderBFS.Node(relX, relZ, 0));
                }
            }
        }
    }

    private static boolean isHole(BlockPos pos, BlockPos above) {
        if (system.isNotFull(pos) && system.canFlow(above, Direction.DOWN)) {
            return true;
        }

        if (IChannelBlock.isChannel(system, pos) && system.isEmpty(above)) {
            return true;
        }

        return FluidUtils.isNaturalWater(system, pos);
    }
}
