package github.jodevnull.waterflow.core.water;

import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.utils.SectionCache;
import github.jodevnull.waterflow.core.feature.EqualizeFeature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class WaterSystem implements WaterManager, WaterAccessor, SectionCache
{
    private static final Map<ResourceKey<Level>, WaterSystem> SYSTEMS = new HashMap<>();
    private final WaterData data;

    @Override @ApiStatus.Internal
    public WaterData data() { return data; }

    public WaterSystem(Level world) {
        this.data = new WaterData(world);
    }

    public static Optional<WaterSystem> of(Level world) {
        if (!world.isClientSide() && Config.isEnabled(world)) {
            return Optional.of(get((ServerLevel) world));
        }

        return Optional.empty();
    }

    public static boolean isLoaded(ServerLevel world) {
        return SYSTEMS.containsKey(world.dimension());
    }

    public static WaterSystem get(ServerLevel world) {
        return SYSTEMS.computeIfAbsent(world.dimension(), k -> new WaterSystem(world));
    }

    public static void onUnload(ServerLevel level) {
        if (isLoaded(level)) {
            SYSTEMS.get(level.dimension()).unload();
            SYSTEMS.remove(level.dimension());
        }
    }

    public static void onUnload() {
        for (final var entry : SYSTEMS.entrySet()) {
            entry.getValue().unload();
        }

        SYSTEMS.clear();
    }

    public void run(BlockPos pos) {
        if (pos.getY() == getMinBuildHeight() + 1) {
            setWater(pos, 0); return;
        }

        final int level = getWaterLevel(pos);

        // If there is no water or the water can't go anywhere, whe don't do anything.
        // Hopefully this will improve performance even more
        if (level <= 0 || FluidUtils.cannotFlow(this, pos)) {
            return;
        }

        if (getBlockState(pos.below()).is(Blocks.LAVA)) {
            data.world.setBlock(pos.below(), Blocks.OBSIDIAN.defaultBlockState(), 11, 11);
            setWater(pos, 0);
        }

        else if (isNotFull(pos.below())) {
            setWater(pos, level - 1);
            addWater(pos.below(), 1);
        }

        else {
            EqualizeFeature.execute(this, pos, level);
        }
    }

    public static boolean run(Level level, BlockPos pos) {
        if (Config.isEnabled(level) && !level.isClientSide()) {
            get((ServerLevel) level).run(pos);
            return true;
        }

        return false;
    }
}
