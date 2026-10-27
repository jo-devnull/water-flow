package github.jodevnull.waterflow.mixin.block;

import github.jodevnull.waterflow.compat.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin
{
    @Redirect(method = "lambda$removeWaterBreadthFirstSearch$1", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean removeFluidStates(Level instance, BlockPos pPos, BlockState pNewState, int pFlags) {
        Compat.Fluidlogged.setFluid(instance, pPos, Fluids.EMPTY.defaultFluidState(), 3, 512);
        return instance.setBlock(pPos, pNewState, pFlags);
    }
}
