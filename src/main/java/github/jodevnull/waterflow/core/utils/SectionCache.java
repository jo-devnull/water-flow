package github.jodevnull.waterflow.core.utils;

import github.jodevnull.waterflow.core.water.WaterData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface SectionCache extends BlockGetter
{
    WaterData data();

    @Override
    default int getHeight() {
        return data().world.getHeight();
    }

    @Override
    default int getMinBuildHeight() {
        return data().world.getMinBuildHeight();
    }

    @Override
    default @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    default LevelChunkSection getSection(long lpos) {
        final var pos = SectionPos.of(lpos);
        final var index = data().world.getSectionIndexFromSectionY(pos.getY());
        final var section = data().world.getChunk(pos.center()).getSection(index);

        section.release();
        section.acquire();

        return section;
    }

    default LevelChunkSection getSection(BlockPos pos) {
        return data().sections.computeIfAbsent(SectionPos.of(pos).asLong(), this::getSection);
    }

    @Override
    default BlockState getBlockState(BlockPos pos) {
        if (pos.getY() < getMinBuildHeight())
            return Blocks.AIR.defaultBlockState();

        return getSection(pos).getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    @Override
    default FluidState getFluidState(BlockPos pos) {
        if (pos.getY() < getMinBuildHeight())
            return Fluids.EMPTY.defaultFluidState();

        return getSection(pos).getFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    default void setBlockState(BlockPos pos, BlockState newState) {
        final var prev = getSection(pos)
            .setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, newState, false);

        if (data().world instanceof ServerLevel world && prev != newState) {
            world.getChunkSource().blockChanged(pos);
            world.sendBlockUpdated(pos, prev, newState,
                Block.UPDATE_IMMEDIATE  |
                Block.UPDATE_CLIENTS    |
                Block.UPDATE_KNOWN_SHAPE
            );
        }
    }

    default void release() {
        data().sections.keySet().forEach(pos -> getSection(pos).release());
        data().sections.clear();
    }
}
