package github.jodevnull.waterflow.core.water;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.feature.EqualizeFeature;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.SeagrassBlock;

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

        if (level <= 0) {
            return;
        }

        if (water.getBlockState(pos.below()).is(Blocks.LAVA)) {
            water.setWater(pos, 0);

            if (level == 8) {
                water.world.setBlock(pos.below(), Blocks.OBSIDIAN.defaultBlockState(), 11, 11);
            }

            waterEvaporationEffect((ServerLevel) water.world, pos);
        }

        // if below is naturally generated water, we merge
        else if (FluidUtils.isNaturalWater(water, pos.below())) {
            water.setWater(pos, 0);
        }

        else if (water.isNotFull(pos.below()) && water.canFlow(pos, Direction.DOWN)) {
            water.setWater(pos, 0);
            water.addWater(pos.below(), level);
        }

        else if (FluidUtils.canFlow(water, pos)) {
            final int height = water.getBlockHeight(pos);

            if (level <= height) {
                displaceFromHeight(water, pos, level, height);
            } else {
                EqualizeFeature.execute(water, pos, level);
            }
        }
    }

    public static void displaceFromHeight(WaterManager water, BlockPos cPos, int cLevel, int cHeight) {
        for (final var dir : Direction.Plane.HORIZONTAL) {
            var rPos = cPos.relative(dir);
            int rLevel = water.getWaterLevel(rPos);
            int rHeight = water.getBlockHeight(rPos);

            if (rLevel <= -1)
                continue;

            if (cLevel <= 0)
                break;

            if ((cHeight + cLevel) > (rLevel + rHeight + 1)) {
                cLevel -= 1;

                water.setWater(cPos, cLevel);
                water.setWater(rPos, rLevel+1);
            }
        }
    }

    public static void spread(Level level, BlockPos pos, int amount) {
        of(level).ifPresent(manager -> {
            manager.setWater(pos, 0);
            manager.addWater(pos.above(), amount);
        });
    }

    public static void waterEvaporationEffect(ServerLevel world, BlockPos pos) {
        final int amount = 3 + world.random.nextInt(2);

        world.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
            amount, 0D, 0.5D, 0D, 0.05D
        );

        world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
            0.8f + world.random.nextFloat() * 0.2f,
            0.9f + world.random.nextFloat() * 0.2f
        );
    }
}
