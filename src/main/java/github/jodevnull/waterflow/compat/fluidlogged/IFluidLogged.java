package github.jodevnull.waterflow.compat.fluidlogged;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.WaterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fml.ModList;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

public interface IFluidLogged
{
    default boolean isLoaded() {
        return false;
    }

    default boolean isFluidloggable(BlockState state) {
        return false;
    }

    default void setFluidState(Level level, LevelChunkSection section, BlockPos pos, FluidState fluidState, int flags) {}

    default void setFluid(Level level, BlockPos pos, FluidState fluidState, int flags, int maxUpdateDepth) {}

    default boolean setWater(WaterManager manager, BlockState state, BlockPos pos, FluidState fluid) {
        return false;
    }

    default FluidState getFluidState(LevelChunkSection section, BlockPos pos) {
        return section.getFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    default boolean canFlow(WaterManager manager, BlockPos center, Direction dir) {
        return true;
    }

    default BlockState handleBlockRemoval(Level instance, BlockPos blockPos, int flags, int maxUpdateDepth) {
        final var fluidState = instance.getFluidState(blockPos);
        return fluidState.createLegacyBlock().trySetValue(TIME, Config.getWaterEvapTime());
    }

    static IFluidLogged loadClass(IFluidLogged result) {
        final String classPath = "github.jodevnull.waterflow.compat.fluidlogged.FluidLoggedImpl";

        if (ModList.get().isLoaded("fluidlogged")) {
            try {
                result = (IFluidLogged) WaterFlow.class.getClassLoader().loadClass(classPath).getConstructor().newInstance();
            } catch (Exception e) {
                WaterFlow.logger.error("Error loading Fluidlogged compat class: {e}", e);
            } finally {
                if (result.isLoaded()) {
                    WaterFlow.logger.info("Successfully loaded Fluidlogged compat class!");
                }
            }
        }

        return result;
    }
}
