package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import de.leximon.fluidlogged.Fluidlogged;
import github.jodevnull.waterflow.config.Config;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Fluidlogged.class)
public class FluidloggedMixin
{
    // @Inject(method = "isFluidloggable", at=@At("HEAD"), cancellable = true)
    // private static void isFluidloggable(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
    //     cir.setReturnValue(Config.isFluidloggable(blockState));
    // }
}
