package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import com.bawnorton.mixinsquared.TargetHandler;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LevelChunkSection.class, priority = 2000)
public abstract class LevelChunkSectionMixin
{
    @Shadow
    public abstract BlockState getBlockState(int pX, int pY, int pZ);

    @TargetHandler(name = "injectGetFluidState", mixin = "de.leximon.fluidlogged.mixin.classes.storing_and_access.LevelChunkSectionMixin")
    @Inject(method = "@MixinSquared:Handler", at=@At("HEAD"), cancellable = true)
    private void patchGetFluidState(int x, int y, int z, CallbackInfoReturnable<FluidState> cir, CallbackInfo ci) {
        final var extension = (LevelChunkSectionExtension) this;
        final var blockState = getBlockState(x, y, z);

        if (Fluidlogged.isFluidloggable(blockState)) {
            FluidState fluidState = extension.getFluidStates().get((short)(x << 8 | y << 4 | z));

            if (fluidState != null) {
                cir.setReturnValue(fluidState); ci.cancel();
            }
        }
    }
}
