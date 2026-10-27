package github.jodevnull.waterflow.mixin.world;

import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

@Mixin(BucketItem.class)
public class BucketItemMixin
{
    @Redirect(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean setBlock(Level level, BlockPos pPos, BlockState pNewState, int pFlags) {
        if (pNewState.is(Blocks.WATER)) {
            if (!level.isClientSide() && Config.isEnabled(level)) {
                final var system = WaterSystem.get((ServerLevel) level);
                final int water = system.getWaterLevel(pPos);

                if (water > 0) {
                    system.addWater(pPos, 8);
                    return true;
                }
            }
        }

        return level.setBlock(pPos, pNewState.trySetValue(TIME, pNewState.is(Blocks.LAVA) ? 1 : Config.getWaterEvapTime()), pFlags);
    }
}
