package github.jodevnull.waterflow.mixin.block;

import github.jodevnull.waterflow.core.WaterSystem;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmBlock.class)
public class FarmBlockMixin
{
    @Redirect(method = "randomTick", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;isNearWater(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean redirectNearWater(LevelReader pLevel, BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos);

        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-4, 0, -4), pPos.offset(4, 1, 4))) {
            final var fluidState = FluidUtils.getFluidState(pLevel, blockpos);

            if (FluidUtils.isFiniteWater(fluidState)) {
                WaterSystem.of((Level) pLevel).ifPresent(manager -> {
                    manager.setWater(blockpos, fluidState.getAmount() - 1);
                });

                return true;
            }

            else if (state.canBeHydrated(pLevel, pPos, fluidState, blockpos)) {
                return true;
            }
        }

        return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(pLevel, pPos);
    }
}
