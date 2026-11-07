package github.jodevnull.waterflow.mixin.fluid;

import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.feat.CommonFeatures;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Fluid.class)
public abstract class FluidMixin
{
    @Inject(method = "isRandomlyTicking", at=@At("HEAD"), cancellable = true)
    private void isRandomlyTicking(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof WaterFluid) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick", at=@At("HEAD"))
    private void onRandomTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom, CallbackInfo ci) {
        if (pLevel.isClientSide() || !Config.isEnabled(pLevel)) return;

        final var manager = WaterSystem.get((ServerLevel) pLevel);

        if (Config.waterEvaporationEnabled()) {
            CommonFeatures.waterEvaporation(manager, pPos);
        }
    }
}
