package github.jodevnull.waterflow.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.block.WaterChannelBlock;
import github.jodevnull.waterflow.core.api.IWaterFlow;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = FluidRenderer.class, remap = false)
public class FluidRendererMixin
{
    @Unique
    private float wf_offset = -1f;

    @Unique
    private boolean wf_aboveChannel = false;

    @Redirect(method = "isFluidOccluded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState checkChannelOcclusion(BlockAndTintGetter instance, BlockPos pos) {
        if (WaterChannelBlock.isChannel(instance, pos)) {
            return instance.getFluidState(pos.above());
        }

        return instance.getFluidState(pos);
    }

    @ModifyReturnValue(method = "fluidHeight", at=@At(value = "RETURN"))
    private float addFluidOffset(float height, @Local(argsOnly = true) BlockAndTintGetter world, @Local(argsOnly = true) BlockPos blockPos, @Local(name = "blockState") BlockState blockState) {
        float offset = IWaterFlow.getFluidOffset(blockState);

        if (this.wf_offset < 0f) {
            this.wf_offset = offset;
            this.wf_aboveChannel = WaterChannelBlock.isChannel(world, blockPos.below());
            return height + offset;
        }

        return height == 0 ? wf_offset : (height + offset);
    }

    @Inject(method = "render", at=@At("TAIL"))
    private void resetFluidOffset(WorldSlice world, FluidState fluidState, BlockPos blockPos, BlockPos offset, ChunkBuildBuffers buffers, CallbackInfo ci) {
        this.wf_offset = -1f;
        this.wf_aboveChannel = false;
    }

    @ModifyExpressionValue(method = "writeQuad", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;getY(I)F"))
    private float setChannelOffset(float y) {
        if (this.wf_aboveChannel && y <= 0.001f) {
            return y - 0.111f;
        }

        return y;
    }
}
