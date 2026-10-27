package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import com.bawnorton.mixinsquared.TargetHandler;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BucketItem.class, priority = 2000)
public class BucketItemMixin
{
    @SuppressWarnings("all")
    @TargetHandler(name = "injectPlaceFluid", mixin = "de.leximon.fluidlogged.mixin.classes.forge.world_interaction.removal_and_placement.BucketItemMixin")
    @Redirect(method = "@MixinSquared:Handler", at= @At(value = "INVOKE", ordinal=0, target = "Lde/leximon/fluidlogged/mixin/extensions/LevelExtension;setFluid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;I)Z"))
    private boolean fixWaterlogging(LevelExtension instance, BlockPos blockPos, FluidState fluidState, int flags) {
        return instance.setFluid(blockPos, FluidUtils.getWater(8), 131);
    }

    @SuppressWarnings("all")
    @TargetHandler(name = "injectPlaceFluid", mixin = "de.leximon.fluidlogged.mixin.classes.forge.world_interaction.removal_and_placement.BucketItemMixin")
    @Redirect(method = "@MixinSquared:Handler", at= @At(value = "INVOKE", ordinal=1, target = "Lde/leximon/fluidlogged/mixin/extensions/LevelExtension;setFluid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;I)Z"))
    private boolean injectFluid2(LevelExtension instance, BlockPos blockPos, FluidState fluidState, int flags) {
        return instance.setFluid(blockPos, fluidState.trySetValue(FluidProperty.TIME, Config.getWaterEvapTime()), 131);
    }
}
