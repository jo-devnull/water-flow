package github.jodevnull.waterflow.compat.fluidlogged;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.mixin.extensions.ServerChunkCacheExtension;
import github.jodevnull.waterflow.core.WaterManager;
import github.jodevnull.waterflow.core.flow.FlowHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class FluidLoggedImpl implements IFluidLogged
{
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean isFluidloggable(BlockState state) {
        return Fluidlogged.isFluidloggable(state);
    }

    @Override
    public boolean canFlow(WaterManager manager, BlockPos center, Direction dir) {
        if (FlowHandler.checkFlowOut(manager.getFlowState(center), dir)) {
            return false;
        }

        return !FlowHandler.checkFlowIn(manager.getFlowState(center.relative(dir)), dir);
    }

    @Override
    public FluidState getFluidState(LevelChunkSection section, BlockPos pos) {
        return ((LevelChunkSectionExtension) section).getFluidStateExact(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    @Override
    public void setFluid(Level level, BlockPos pos, FluidState fluidState, int flags, int maxUpdateDepth) {
        ((LevelExtension) level).setFluid(pos, Fluids.EMPTY.defaultFluidState(), flags, maxUpdateDepth);
    }

    @Override
    public BlockState handleBlockRemoval(Level instance, BlockPos blockPos, int flags, int maxUpdateDepth) {
        return Fluidlogged.Internal.handleBlockRemoval(instance, blockPos, flags, maxUpdateDepth);
    }

    @Override
    public void setFluidState(Level world, LevelChunkSection section, BlockPos pos, FluidState fluidState, int flags) {
        final var prev = ((LevelChunkSectionExtension) section)
            .setFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, fluidState);

        if (world instanceof ServerLevel level && prev != fluidState) {
            ((ServerChunkCacheExtension) level.getChunkSource()).fluidChanged(pos);
            ((LevelExtension) level).sendFluidUpdated(pos, flags);
        }
    }
}
