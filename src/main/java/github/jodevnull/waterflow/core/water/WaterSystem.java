package github.jodevnull.waterflow.core.water;

import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.feature.EqualizeFeature;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class WaterSystem
{
    private static final Map<ResourceKey<Level>, WaterManager> SYSTEM = new HashMap<>();

    public static Optional<WaterManager> of(Level world) {
        if (!world.isClientSide() && Config.isEnabled(world)) {
            return Optional.of(get((ServerLevel) world));
        }

        return Optional.empty();
    }

    public static boolean isLoaded(ServerLevel world) {
        return SYSTEM.containsKey(world.dimension());
    }

    public static WaterManager get(ServerLevel world) {
        return SYSTEM.computeIfAbsent(world.dimension(), k -> new WaterManager(world));
    }

    public static void onUnload(ServerLevel level) {
        if (isLoaded(level)) {
            SYSTEM.get(level.dimension()).unload();
            SYSTEM.remove(level.dimension());
        }
    }

    public static void onUnload() {
        for (final var entry : SYSTEM.entrySet()) {
            entry.getValue().unload();
        }

        SYSTEM.clear();
    }

    public static boolean run(Level level, BlockPos pos) {
        if (Config.isEnabled(level) && !level.isClientSide()) {
            run(get((ServerLevel) level), pos);
            return true;
        }

        return false;
    }

    public static void run(WaterManager water, BlockPos pos) {
        if (pos.getY() == water.getMinBuildHeight() + 1) {
            water.setWater(pos, 0); return;
        }

        final int level = water.getWaterLevel(pos);

        // If there is no water or the water can't go anywhere, whe don't do anything.
        // Hopefully this will improve performance even more
        if (level <= 0 || FluidUtils.cannotFlow(water, pos)) {
            return;
        }

        if (water.getBlockState(pos.below()).is(Blocks.LAVA)) {
            water.world.setBlock(pos.below(), Blocks.OBSIDIAN.defaultBlockState(), 11, 11);
            water.setWater(pos, 0);
        }

        else if (water.isNotFull(pos.below())) {
            water.setWater(pos, 0);
            water.addWater(pos.below(), level);
        }

        else {
            EqualizeFeature.execute(water, pos, level);
        }
    }
}
