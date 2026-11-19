package github.jodevnull.waterflow.foundation.mixin.extension.waterlogging.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.jodevnull.waterflow.extension.waterlogging.Waterlogging;
import github.jodevnull.waterflow.extension.waterlogging.interfaces.WL$LevelChunkSection;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements WL$LevelChunkSection
{
    @Unique
    private Short2ByteMap wf_waterLogMap;

    @Override
    public void wf_createWaterlogMap() {
        this.wf_waterLogMap = new Short2ByteOpenHashMap();
        this.wf_waterLogMap.defaultReturnValue((byte) 0);
    }

    @Override
    public Short2ByteMap wf_getWaterLogMap() {
        return wf_waterLogMap;
    }

    @Override
    public FluidState wf_setWaterState(int x, int y, int z, FluidState state) {
        final byte packed = state.isEmpty()
            ? wf_waterLogMap.remove((short) (x << 8 | y << 4 | z))
            : wf_waterLogMap.put   ((short) (x << 8 | y << 4 | z), Waterlogging.pack(state));

        return Waterlogging.getWaterState(packed);
    }

    @Override
    public FluidState wf_getWaterState(int x, int y, int z) {
        return Waterlogging.getWaterState(wf_waterLogMap.get((short) (x << 8 | y << 4 | z)));
    }

    @Inject(method = "<init>(Lnet/minecraft/core/Registry;)V", at = @At("RETURN"))
    private void injectInit(Registry<Biome> registry, CallbackInfo ci) {
        wf_createWaterlogMap();
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void injectGetFluidState(int x, int y, int z, CallbackInfoReturnable<FluidState> cir) {
        final FluidState waterState = this.wf_getWaterState(x, y, z);

        if (waterState.isEmpty())
            return;

        cir.setReturnValue(waterState);
    }

    @ModifyReturnValue(method = "hasOnlyAir", at = @At("RETURN"))
    private boolean injectHasOnlyAir(boolean original) {
        return original && this.wf_waterLogMap.isEmpty();
    }

    @ModifyReturnValue(method = "getSerializedSize", at = @At("RETURN"))
    private int injectGetSerializedSize(int original) {
        final int mapsize = this.wf_waterLogMap.size() * (Short.BYTES + Byte.BYTES);
        return original + Short.BYTES + mapsize;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void injectWrite(FriendlyByteBuf buf, CallbackInfo ci) {
        buf.writeShort(this.wf_waterLogMap.size());

        for (final var entry : this.wf_waterLogMap.short2ByteEntrySet()) {
            buf.writeShort(entry.getShortKey());
            buf.writeByte(entry.getByteValue());
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void injectRead(FriendlyByteBuf buf, CallbackInfo ci) {
        this.wf_waterLogMap.clear();

        short size = buf.readShort();

        for (short i = 0; i < size; i++) {
            short pos = buf.readShort();
            this.wf_waterLogMap.put(pos, buf.readByte());
        }
    }
}
