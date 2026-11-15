package github.jodevnull.waterflow.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.core.IWaterSystem;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin
{
    @Redirect(method = "moveBlocks", at= @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean onMoveBlocks(Level pLevel, BlockPos pPos, BlockState pNewState, int pFlags, @Local(argsOnly = true) Direction pFacing) {
        final var fluid = FluidUtils.getFluidState(pLevel, pPos);

        if (!fluid.isEmpty() && !pLevel.isClientSide()) {
            IWaterSystem.get((ServerLevel) pLevel).ifPresent(system -> {
                system.setWater(pPos, 0);
                system.addWater(pPos.relative(pFacing), fluid.getAmount(), pFacing);
            });
        }

        return pLevel.setBlock(pPos, pNewState, pFlags);
    }
}
