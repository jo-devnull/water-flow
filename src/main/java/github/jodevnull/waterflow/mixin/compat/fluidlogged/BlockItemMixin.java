package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.flow.IFlowHandler;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import com.bawnorton.mixinsquared.TargetHandler;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockItem.class, priority = 2000)
public abstract class BlockItemMixin
{
    @Shadow
    protected abstract boolean placeBlock(BlockPlaceContext pContext, BlockState pState);

    @TargetHandler(name = "redirectFluidPlacement", mixin = "de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement.BlockItemMixin")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void spreadWaterIfPossible(BlockItem instance, BlockPlaceContext context, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final FluidState fluidState = FluidUtils.getFluidState(level, pos);
        final int blockHeight = ((IFlowHandler) blockState.getBlock()).wf_height(blockState);

        if (FluidUtils.isNaturalWater(fluidState))
            return;

        if (placeBlock(context, blockState)) {
            if (!fluidState.isEmpty()) {
                if (Fluidlogged.isFluidloggable(blockState) && blockHeight == 0) {
                    ((LevelExtension) level).setFluid(pos, fluidState, 131);
                } else if (!level.isClientSide()) {
                    if (!blockState.is(Blocks.SPONGE)) {
                        WaterSystem.spread(level, pos, fluidState.getAmount());
                    }
                }
            }

            cir.setReturnValue(true);
        }

        else {
            cir.setReturnValue(false);
        }
    }
}
