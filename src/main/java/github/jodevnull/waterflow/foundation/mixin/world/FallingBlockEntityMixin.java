package github.jodevnull.waterflow.foundation.mixin.world;

import github.jodevnull.waterflow.foundation.core.api.IWaterSystem;
import github.jodevnull.waterflow.foundation.core.fluid.FluidProperty;
import github.jodevnull.waterflow.foundation.core.fluid.FluidUtils;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin
{
    @Redirect(method = "tick", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Fallable;onLand(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/item/FallingBlockEntity;)V"))
    private void onLand(Fallable instance, Level pLevel, BlockPos pPos, BlockState pState, BlockState pReplaceableState, FallingBlockEntity pFallingBlock) {
        instance.onLand(pLevel, pPos, pState, pReplaceableState, pFallingBlock);

        if (pReplaceableState.isAir())
            return;

        final var fluidState = FluidProperty.getFluidState(pReplaceableState);

        if (!FluidUtils.isFiniteWater(fluidState)) return;

        if (pLevel instanceof ServerLevel world) {
            IWaterSystem.setWaterLevel(world, pPos, 0);
            IWaterSystem.spread(world, pPos, fluidState.getAmount());
        }
    }
}
