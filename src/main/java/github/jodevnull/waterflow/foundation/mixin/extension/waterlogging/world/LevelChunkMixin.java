package github.jodevnull.waterflow.foundation.mixin.extension.waterlogging.world;

import github.jodevnull.waterflow.extension.waterlogging.Waterlogging;
import github.jodevnull.waterflow.extension.waterlogging.interfaces.WL$LevelChunk;
import github.jodevnull.waterflow.extension.waterlogging.interfaces.WL$LevelChunkSection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess implements WL$LevelChunk
{
    @Shadow @Final Level level;

    public LevelChunkMixin(ChunkPos pChunkPos, UpgradeData pUpgradeData, LevelHeightAccessor pLevelHeightAccessor, Registry<Biome> pBiomeRegistry, long pInhabitedTime, @Nullable LevelChunkSection[] pSections, @Nullable BlendingData pBlendingData) {
        super(pChunkPos, pUpgradeData, pLevelHeightAccessor, pBiomeRegistry, pInhabitedTime, pSections, pBlendingData);
    }

    @Override
    public FluidState wf_setWaterState(BlockPos pos, FluidState water) {
        int y = pos.getY();

        LevelChunkSection levelChunkSection = this.getSection(this.getSectionIndex(y));
        boolean hasOnlyAir = levelChunkSection.hasOnlyAir();

        if (levelChunkSection.hasOnlyAir() && water.isEmpty())
            return null;

        int rx = pos.getX() & 15;
        int ry = y & 15;
        int rz = pos.getZ() & 15;

        final FluidState prevFluidState = ((WL$LevelChunkSection) levelChunkSection).wf_setWaterState(rx, ry, rz, water);
        if (prevFluidState == water)
            return null;

        BlockState blockState = levelChunkSection.getBlockState(rx, ry, rz);
        this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING).update(rx, ry, rz, blockState);
        this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES).update(rx, ry, rz, blockState);
        this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR).update(rx, ry, rz, blockState);
        this.heightmaps.get(Heightmap.Types.WORLD_SURFACE).update(rx, ry, rz, blockState);

        boolean newHasOnlyAir = levelChunkSection.hasOnlyAir();
        if (hasOnlyAir != newHasOnlyAir)
            this.level.getChunkSource().getLightEngine().updateSectionStatus(pos, newHasOnlyAir);

        if (Waterlogging.hasDifferentLightEmission(level, pos, prevFluidState, water)) {
            ProfilerFiller profilerFiller = this.level.getProfiler();
            profilerFiller.push("updateSkyLightSources");
            this.skyLightSources.update(this, rx, ry, rz);
            profilerFiller.popPush("queueCheckLight");
            this.level.getChunkSource().getLightEngine().checkBlock(pos);
            profilerFiller.pop();
        }

        this.unsaved = true;
        return prevFluidState;
    }
}
