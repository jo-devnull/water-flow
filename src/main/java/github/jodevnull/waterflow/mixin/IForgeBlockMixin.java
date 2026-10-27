package github.jodevnull.waterflow.mixin;

import github.jodevnull.waterflow.compat.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IForgeBlock.class, remap = false)
public interface IForgeBlockMixin
{
    @Shadow Block self();

    /**
     * @author jo-devnull
     * @reason handle block removal
     */
    @Overwrite()
    default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        self().playerWillDestroy(level, pos, state, player);
        return level.setBlock(pos, Compat.Fluidlogged.handleBlockRemoval(level, pos, 11, 512), level.isClientSide ? 11 : 3);
    }
}
