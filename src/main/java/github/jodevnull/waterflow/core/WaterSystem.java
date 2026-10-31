package github.jodevnull.waterflow.core;

import github.jodevnull.waterflow.ModTags;
import github.jodevnull.waterflow.block.IChannelBlock;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.core.feat.EqualizeFeature;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.utils.WaterEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class WaterSystem
{
    private static final Map<ResourceKey<Level>, WaterManager> SYSTEM = new HashMap<>();

    public static boolean run(Level level, BlockPos pos) {
        if (FluidUtils.shouldSkipVanillaBehavior(level, pos)) {
            return true;
        }

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
        final BlockState below = water.getBlockState(pos.below());

        if (level <= 0) {
            return;
        }

        else if (below.getBlock() instanceof ChannelBlock) {
            IChannelBlock.flowWaterAbove(water, below, pos, level);
        }

        else if (below.is(Blocks.LAVA)) {
            water.setWater(pos, 0);

            if (level == 8) {
                water.world.setBlockAndUpdate(pos.below(), Blocks.OBSIDIAN.defaultBlockState());
            } else {
                 final int time = FluidProperty.timeof(below);

                 if (time < FluidProperty.MAX_TIME) {
                     water.setBlockState(pos.below(), below.trySetValue(FluidProperty.TIME, time+1));
                 } else {
                     water.world.setBlockAndUpdate(pos.below(), Blocks.OBSIDIAN.defaultBlockState());
                 }
            }

            WaterEffects.fizz((ServerLevel) water.world, pos);
        }

        else if (below.getOptionalValue(FarmBlock.MOISTURE).orElse(8) < 7) {
            water.setWater(pos, level - 1);
            water.world.setBlock(pos.below(), below.trySetValue(FarmBlock.MOISTURE, 7), 2);
        }

        // if below is naturally generated water, we merge it
        // Only if not a #needs_water_source block (like kelp)
        else if (FluidUtils.isNaturalWater(water, pos.below())) {
            if (!below.is(ModTags.Blocks.NEEDS_WATER_SOURCE)) {
                water.setWater(pos, 0);
            }
        }

        else if (water.isNotFull(pos.below()) && water.canFlow(pos, Direction.DOWN)) {
            water.setWater(pos, 0);
            water.addWater(pos.below(), level);
        }

        else {
            EqualizeFeature.execute(water, pos, level);
        }
    }

    public static void spread(WaterManager water, BlockPos pos, int amount, Direction dir) {
        water.setWater(pos, 0);

        if (water.getWaterLevel(pos.relative(dir)) > -1) {
            water.addWater(pos.relative(dir), amount, dir);
        } else {
            water.addWater(pos.above(), amount, dir);
        }
    }

    public static void spread(Level level, BlockPos pos, int amount) {
        of(level).ifPresent(manager -> spread(manager, pos, amount, Direction.UP));
    }

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
}
