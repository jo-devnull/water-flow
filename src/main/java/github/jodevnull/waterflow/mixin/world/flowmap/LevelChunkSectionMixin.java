package github.jodevnull.waterflow.mixin.world.flowmap;

import github.jodevnull.waterflow.core.flow.ext.FM$LevelChunkSection;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements FM$LevelChunkSection
{
    @Unique
    private Short2ByteMap wf_flowMap;

    @Override
    public Short2ByteMap wf_createFlowMap() {
        wf_flowMap = new Short2ByteOpenHashMap();
        wf_flowMap.defaultReturnValue((byte) 0);
        return wf_flowMap;
    }

    @Override
    public Short2ByteMap wf_getFlowMap() {
        return wf_flowMap;
    }

    @Override
    public byte wf_getFlow(int x, int y, int z) {
        return this.wf_flowMap.get((short) (x << 8 | y << 4 | z));
    }

    @Override
    public byte wf_setFlow(int x, int y, int z, int flow) {
        return flow == 0
            ? wf_flowMap.remove((short) (x << 8 | y << 4 | z))
            : wf_flowMap.put((short) (x << 8 | y << 4 | z), (byte) flow);
    }

    @Inject(method = "<init>(Lnet/minecraft/core/Registry;)V", at=@At("RETURN"))
    private void injectInit(Registry<Biome> pBiomeRegistry, CallbackInfo ci) {
        this.wf_createFlowMap();
    }

//    @Inject(method = "getSerializedSize", at=@At("RETURN"), cancellable = true)
//    private void injectSerializedSize(CallbackInfoReturnable<Integer> cir) {
//        int flowMapSize = Short.BYTES + this.wf_flowMap.size() * (Short.BYTES + Integer.BYTES);
//        cir.setReturnValue(cir.getReturnValue() + flowMapSize);
//    }

//    @Inject(method = "write", at=@At("TAIL"))
//    private void injectWrite(FriendlyByteBuf pBuffer, CallbackInfo ci) {
//        pBuffer.writeShort(this.wf_flowMap.size());
//
//        for (final var entry : this.wf_flowMap.short2ByteEntrySet()) {
//            pBuffer.writeShort(entry.getShortKey());
//            pBuffer.writeByte(entry.getByteValue());
//        }
//    }
//
//    @Inject(method = "read", at=@At("TAIL"))
//    private void injectRead(FriendlyByteBuf pBuffer, CallbackInfo ci) {
//        this.wf_flowMap.clear();
//        final short size = pBuffer.readShort();
//
//        for (short i = 0; i < size; i++) {
//            this.wf_flowMap.put(pBuffer.readShort(), pBuffer.readByte());
//        }
//    }
}
