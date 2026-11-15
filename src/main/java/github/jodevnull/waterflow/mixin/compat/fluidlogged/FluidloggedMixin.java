package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import de.leximon.fluidlogged.Fluidlogged;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Fluidlogged.class)
public class FluidloggedMixin
{
    // @Inject(method = "isFluidloggable", at=@At("HEAD"), cancellable = true)
    // private static void isFluidloggable(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
    //     cir.setReturnValue(Config.isFluidloggable(blockState));
    // }
}
