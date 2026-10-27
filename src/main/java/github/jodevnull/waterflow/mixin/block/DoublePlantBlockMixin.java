package github.jodevnull.waterflow.mixin.block;

import github.jodevnull.waterflow.compat.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin
{
    @Redirect(method = "preventCreativeDropFromBottomPart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean onLevelSetWaterBlock(Level instance, BlockPos pPos, BlockState pNewState, int pFlags) {
        return instance.setBlock(pPos, Compat.Fluidlogged.handleBlockRemoval(instance, pPos, pFlags, 512), pFlags);
    }
}
