package github.jodevnull.waterflow.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    private boolean onMoveBlocks(Level instance, BlockPos pPos, BlockState pNewState, int pFlags, @Local(argsOnly = true) Direction pFacing) {
        final var fluid = FluidUtils.getFluidState(instance, pPos);

        if (!fluid.isEmpty()) {
            WaterSystem.of(instance).ifPresent(manager -> {
                manager.setWater(pPos, 0);
                manager.addWater(pPos.relative(pFacing), fluid.getAmount(), pFacing);
            });
        }

        return instance.setBlock(pPos, pNewState, pFlags);
    }
}
