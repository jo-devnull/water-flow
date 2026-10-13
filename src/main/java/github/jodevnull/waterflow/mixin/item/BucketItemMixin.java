package github.jodevnull.waterflow.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BucketItem.class)
public class BucketItemMixin
{
    @ModifyExpressionValue(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;createLegacyBlock()Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState bucketPlace(BlockState original) {
        // Set time to max if is water or lava
        return original.trySetValue(FluidProperty.TIME, original.is(Blocks.LAVA) ? 1 : FluidProperty.MAX_TIME);
    }
}
