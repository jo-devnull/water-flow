package github.jodevnull.waterflow.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.core.utils.IFlowHandler;
import github.jodevnull.waterflow.core.utils.Utils;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin
{
    @Unique
    private static final float mercury$MAX_HEIGHT = 8/9.0F;

    @Redirect(method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)F",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getOwnHeight()F"))
    private float accountForVolume(FluidState fluidState, @Local(argsOnly = true) BlockState pBlockState) {
        if (fluidState.isEmpty()) return fluidState.getOwnHeight();

        final float blockHeight = ((IFlowHandler) pBlockState.getBlock()).wf$height(pBlockState);

        if (fluidState.getAmount() <= blockHeight) {
            final float height = fluidState.getOwnHeight() + (1+blockHeight / 8.0F);
            return Utils.clamp(height, 0F, mercury$MAX_HEIGHT);
        }

        return fluidState.getOwnHeight();
    }
}