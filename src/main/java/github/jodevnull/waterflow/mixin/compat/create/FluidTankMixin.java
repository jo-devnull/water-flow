package github.jodevnull.waterflow.mixin.compat.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidTank.class)
public class FluidTankMixin
{
    @Shadow
    @NotNull
    protected FluidStack fluid;

    @ModifyExpressionValue(remap = false, method = "fill", at= @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;isFluidEqual(Lnet/minecraftforge/fluids/FluidStack;)Z"))
    public boolean ifc$checkWater(boolean original, @Local(argsOnly = true) FluidStack resource) {
        // if both are water
        if (FluidHelper.isWater(fluid.getFluid()) && FluidHelper.isWater(resource.getFluid())) {
            return true;
        }

        return original;
    }
}
