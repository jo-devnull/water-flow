package github.jodevnull.waterflow.mixin.client.embeddium;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.core.utils.IFlowHandler;
import github.jodevnull.waterflow.core.utils.Utils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets = "me/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer", remap = false)
public class FluidRendererMixin
{
    @Unique
    private static final float mercury$MAX_HEIGHT = 8/9.0F;

    @Redirect(method = "fluidHeight", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getOwnHeight()F"))
    private float accountForVolume(FluidState fluidState, @Local(name = "blockState") BlockState blockState) {
        if (fluidState.isEmpty()) return fluidState.getOwnHeight();

        final float blockHeight = ((IFlowHandler) blockState.getBlock()).wf$height(blockState);

        if (fluidState.getAmount() <= blockHeight) {
            final float height = fluidState.getOwnHeight() + (1+blockHeight / 8.0F);
            return Utils.clamp(height, 0F, mercury$MAX_HEIGHT);
        }

        return fluidState.getOwnHeight();
    }
}
