package github.jodevnull.waterflow.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.core.api.IWaterFlow;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = FluidRenderer.class, remap = false)
public class FluidRendererMixin
{
    @Shadow
    @Final
    private BlockPos.MutableBlockPos scratchPos;
    @Unique
    private float wf_fluidOffset = -1f;

    @Unique
    private float wf_fluidHeight = -1f;

    @Redirect(method = "fluidHeight", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getOwnHeight()F"))
    private float addFluidOffset(FluidState instance, @Local(name = "blockState") BlockState blockState) {
        final float offset = ((IWaterFlow) blockState.getBlock()).fluidOffset(blockState);
        final float height = instance.getOwnHeight();

        if (wf_fluidOffset < 0 || wf_fluidHeight < 0) {
            this.wf_fluidOffset = offset;
            this.wf_fluidHeight = height;
        }

        if ((offset + height) < wf_fluidOffset) {
            return -1f;
        }

        return height;
    }

    @Redirect(method = "writeQuad", at= @At(value = "FIELD", target = "Lme/jellysquid/mods/sodium/client/render/chunk/vertex/format/ChunkVertexEncoder$Vertex;y:F", opcode = Opcodes.PUTFIELD))
    private void setYOffset(ChunkVertexEncoder.Vertex instance, float value) {
        if (wf_fluidHeight < wf_fluidOffset) {
            instance.y = value + this.wf_fluidOffset;
        } else {
            instance.y = value;
        }
    }

    @Inject(method = "render", at=@At("TAIL"))
    private void resetFluidOffset(WorldSlice world, FluidState fluidState, BlockPos blockPos, BlockPos offset, ChunkBuildBuffers buffers, CallbackInfo ci) {
        this.wf_fluidOffset = -1f;
        this.wf_fluidHeight = -1f;
    }

    @Inject(method = "isFluidOccluded", at=@At(value = "RETURN", ordinal = 0), cancellable = true)
    private void checkFluidOffset(BlockAndTintGetter world, int x, int y, int z, Direction dir, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (Direction.Plane.HORIZONTAL.test(dir)) {
            final var centerOffset = wf_getFluidOffset(world, x, y, z);
            final var neighborOffset = wf_getFluidOffset(world, x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());
            final var fluidHeight = world.getFluidState(scratchPos.set(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ()))
                .getOwnHeight();

            if ((fluidHeight + neighborOffset) < centerOffset) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private float wf_getFluidOffset(BlockAndTintGetter world, int x, int y, int z) {
        final var blockState = world.getBlockState(scratchPos.set(x, y, z));
        return ((IWaterFlow) blockState.getBlock()).fluidOffset(blockState);
    }
}
