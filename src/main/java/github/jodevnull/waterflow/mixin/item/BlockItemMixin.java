package github.jodevnull.waterflow.mixin.item;

import github.jodevnull.waterflow.core.WaterSystem;
import github.jodevnull.waterflow.core.api.IWaterFlow;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin
{
    @Shadow
    protected abstract boolean placeBlock(BlockPlaceContext pContext, BlockState pState);

    @Redirect(method = "place", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean spreadWaterIfPossible(BlockItem instance, BlockPlaceContext context, BlockState blockState) {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final FluidState fluidState = FluidUtils.getFluidState(level, pos);
        final float offset = IWaterFlow.getFluidOffset(blockState);

        if (placeBlock(context, blockState)) {
            if (FluidUtils.isNaturalWater(fluidState))
                return true;

            if (!fluidState.isEmpty()) {
                if (!level.isClientSide() && !blockState.is(Blocks.SPONGE)) {
                    WaterSystem.spread(level, pos, fluidState.getAmount());
                }
            }

            return true;
        }

        return false;
    }
}
