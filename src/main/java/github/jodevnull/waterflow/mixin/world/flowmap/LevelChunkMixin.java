package github.jodevnull.waterflow.mixin.world.flowmap;

import github.jodevnull.waterflow.core.flow.ext.FM$LevelChunk;
import github.jodevnull.waterflow.core.flow.ext.FM$LevelChunkSection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess implements FM$LevelChunk
{
    public LevelChunkMixin(ChunkPos pChunkPos, UpgradeData pUpgradeData, LevelHeightAccessor pLevelHeightAccessor, Registry<Biome> pBiomeRegistry, long pInhabitedTime, @Nullable LevelChunkSection[] pSections, @Nullable BlendingData pBlendingData) {
        super(pChunkPos, pUpgradeData, pLevelHeightAccessor, pBiomeRegistry, pInhabitedTime, pSections, pBlendingData);
    }

    @Override
    public Direction wf_getFlow(BlockPos pos) {
        int y = pos.getY();

        final var section = this.getSection(this.getSectionIndex(y));

        if (section.hasOnlyAir())
            return Direction.DOWN;

        int rx = pos.getX() & 15;
        int ry = y & 15;
        int rz = pos.getZ() & 15;

        final byte flowIdx = ((FM$LevelChunkSection) section).wf_getFlow(rx, ry, rz);
        return Direction.from3DDataValue(flowIdx);
    }

    @Override
    public Direction wf_setFlow(BlockPos pos, Direction flow) {
        int y = pos.getY();

        final var section = this.getSection(this.getSectionIndex(y));

        if (section.hasOnlyAir())
            return Direction.DOWN;

        int rx = pos.getX() & 15;
        int ry = y & 15;
        int rz = pos.getZ() & 15;

        byte prevFlowIndex = ((FM$LevelChunkSection) section).wf_setFlow(rx, ry, rz, flow.get3DDataValue());

        if (prevFlowIndex == flow.get3DDataValue()) {
            return flow;
        }

        this.setUnsaved(true);
        return Direction.from3DDataValue(prevFlowIndex);
    }
}
