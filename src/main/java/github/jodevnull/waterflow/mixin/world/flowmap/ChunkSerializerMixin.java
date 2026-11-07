package github.jodevnull.waterflow.mixin.world.flowmap;

import com.mojang.serialization.Codec;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.core.flow.ext.FM$LevelChunkSection;
import github.jodevnull.waterflow.utils.PackedValue;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin
{
    @Unique
    private static String WF_NBT_KEY = WaterFlow.MODID + ".flowmap";

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectRead(ServerLevel pLevel, PoiManager pPoiManager, ChunkPos pPos, CompoundTag pTag, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkpos, UpgradeData upgradedata, boolean flag, ListTag listtag, int i, LevelChunkSection[] alevelchunksection, boolean flag1, ChunkSource chunksource, LevelLightEngine levellightengine, Registry registry, Codec codec, boolean flag2, int j, CompoundTag compoundtag, int k, int l, PalettedContainer palettedcontainer, PalettedContainerRO palettedcontainerro, LevelChunkSection levelchunksection, SectionPos sectionpos) {
        final Short2ByteMap flowMap = ((FM$LevelChunkSection) levelchunksection).wf_createFlowMap();

        if (compoundtag.contains(WF_NBT_KEY, Tag.TAG_INT_ARRAY)) {
            final int[] data = compoundtag.getIntArray(WF_NBT_KEY);

            for (final int packedValue : data) {
                short pos = PackedValue.ShortByte.getShort(packedValue);
                byte flow = PackedValue.ShortByte.getByte(packedValue);

                if (flow != 0) {
                    flowMap.put(pos, flow);
                }
            }
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;encodeStart(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectWrite(ServerLevel pLevel, ChunkAccess pChunk, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkpos, CompoundTag compoundtag, BlendingData blendingdata, BelowZeroRetrogen belowzeroretrogen, UpgradeData upgradedata, LevelChunkSection[] alevelchunksection, ListTag listtag, LevelLightEngine levellightengine, Registry registry, Codec codec, boolean flag, int i, int j, boolean flag1, DataLayer datalayer, DataLayer datalayer1, CompoundTag compoundtag1, LevelChunkSection levelchunksection) {
        final Short2ByteMap flowMap = ((FM$LevelChunkSection) levelchunksection).wf_getFlowMap();

        final int[] data = new int[flowMap.size()];

        int n = 0;
        for (final var entry : flowMap.short2ByteEntrySet()) {
            data[n++] = PackedValue.ShortByte.pack(entry.getShortKey(), entry.getByteValue());
        }

        compoundtag1.putIntArray(WF_NBT_KEY, data);
    }
}
