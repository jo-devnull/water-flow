package github.jodevnull.waterflow.foundation.mixin.extension.waterlogging.world;

import github.jodevnull.waterflow.extension.waterlogging.Waterlogging;
import github.jodevnull.waterflow.extension.waterlogging.interfaces.WL$Level;
import github.jodevnull.waterflow.extension.waterlogging.interfaces.WL$LevelChunk;
import github.jodevnull.waterflow.foundation.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor, AutoCloseable, WL$Level
{
    @Shadow public abstract LevelChunk getChunkAt(BlockPos blockPos);

    @Override
    public boolean wf_setWater(BlockPos pos, FluidState water, int flags, int maxUpdateDepth) {
        final Level self = (Level) (Object) this;

        if ((flags & Block.UPDATE_MOVE_BY_PISTON) != 0)
            throw new IllegalArgumentException("Flag UPDATE_MOVE_BY_PISTON (0x40) is not permitted for fluid state updates");

        if (self.isOutsideBuildHeight(pos))
            return false;

        if (!self.isClientSide && self.isDebug())
            return false;


        LevelChunk levelChunk = self.getChunkAt(pos);
        FluidState prevFluidState = ((WL$LevelChunk) levelChunk).wf_setWaterState(pos, water);

        if (prevFluidState == null)
            return false;


        if (prevFluidState != water)
            wf_setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());

        if ((flags & Block.UPDATE_CLIENTS) != 0
            && (!self.isClientSide || (flags & Block.UPDATE_INVISIBLE) == 0)
            && (self.isClientSide || levelChunk.getFullStatus() != null && levelChunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
            this.wf_sendWaterUpdated(pos, flags);
        }

        BlockState blockState = self.getBlockState(pos);

        if ((flags & Block.UPDATE_NEIGHBORS) != 0) {
            self.blockUpdated(pos, blockState.getBlock());
            if (!self.isClientSide && blockState.hasAnalogOutputSignal())
                self.updateNeighbourForOutputSignal(pos, blockState.getBlock());
        }

        if ((flags & Waterlogging.UPDATE_SCHEDULE_FLUID_TICK) != 0) {
            Fluid fluid = water.getType();
            scheduleTick(pos, fluid, fluid.getTickDelay(self));
        }

        if ((flags & Block.UPDATE_KNOWN_SHAPE) == 0 && maxUpdateDepth > 0) {
            int updateNeighbourFlags = flags & -34;
            blockState.updateIndirectNeighbourShapes(self, pos, updateNeighbourFlags, maxUpdateDepth - 1); // TODO: might be handled wrong
            blockState.updateNeighbourShapes(self, pos, updateNeighbourFlags, maxUpdateDepth - 1);
            blockState.updateIndirectNeighbourShapes(self, pos, updateNeighbourFlags, maxUpdateDepth - 1);
        }

        return true;
    }

    @Override
    public boolean wf_setBlockAndInsertWaterIfPossible(BlockPos pos, BlockState state, int flags) {
        FluidState fluidState = getFluidState(pos);

        boolean success = setBlock(pos, state, flags);

        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
            return success;

        if (success && Config.isWaterloggable(state))
            wf_setWater(pos, fluidState, flags | Waterlogging.UPDATE_SCHEDULE_FLUID_TICK);

        return success;
    }
}