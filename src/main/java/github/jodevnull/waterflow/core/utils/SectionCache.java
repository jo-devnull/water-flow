package github.jodevnull.waterflow.core.utils;

import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.mixin.extensions.ServerChunkCacheExtension;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
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
public abstract class SectionCache implements BlockGetter
{
    private final Long2ObjectMap<LevelChunkSection>
        sections = new Long2ObjectOpenHashMap<>();

    public final Level world;

    public SectionCache(Level world) {
        this.world = world;
    }

    @Override
    public int getHeight() {
        return world.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return world.getMinBuildHeight();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    public LevelChunkSection getSection(long lpos) {
        final var pos = SectionPos.of(lpos);
        final var index = world.getSectionIndexFromSectionY(pos.getY());
        final var section = world.getChunk(pos.center()).getSection(index);

        section.release();
        section.acquire();

        return section;
    }

    public LevelChunkSection getSection(BlockPos pos) {
        return sections.computeIfAbsent(SectionPos.of(pos).asLong(), this::getSection);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.getY() < getMinBuildHeight())
            return Blocks.AIR.defaultBlockState();

        return getSection(pos).getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (pos.getY() < getMinBuildHeight())
            return Fluids.EMPTY.defaultFluidState();

        return ((LevelChunkSectionExtension) getSection(pos)).getFluidStateExact(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public void setBlockState(BlockPos pos, BlockState newState) {
        final var prev = getSection(pos)
            .setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, newState, false);

        if (world instanceof ServerLevel level && prev != newState) {
            level.getChunkSource().blockChanged(pos);
            level.sendBlockUpdated(pos, prev, newState,
                Block.UPDATE_IMMEDIATE  |
                Block.UPDATE_CLIENTS    |
                Block.UPDATE_KNOWN_SHAPE
            );
        }
    }

    public void setFluidState(BlockPos pos, FluidState newState) {
        final var prev = ((LevelChunkSectionExtension) getSection(pos))
            .setFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, newState);

        if (world instanceof ServerLevel level && prev != newState) {
            ((ServerChunkCacheExtension) level.getChunkSource()).fluidChanged(pos);
            ((LevelExtension) level).sendFluidUpdated(pos,
                Block.UPDATE_IMMEDIATE  |
                Block.UPDATE_CLIENTS    |
                Block.UPDATE_KNOWN_SHAPE
            );
        }
    }

    public void releaseSections() {
        sections.forEach((pos, section) -> section.release());
        sections.clear();
    }
}
