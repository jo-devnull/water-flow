package github.jodevnull.waterflow.mixin.fluid;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static github.jodevnull.waterflow.core.fluid.FluidProperty.TIME;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin extends Block
{
    @Shadow
    @Final
    public static IntegerProperty LEVEL;

    public LiquidBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "createBlockStateDefinition", at=@At("TAIL"))
    private void injectProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(TIME);
    }

    @Redirect(method = "<init>*",
    at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void setPropertyDefaults(LiquidBlock instance, BlockState state) {
        registerDefaultState(stateDefinition.any()
            .setValue(LEVEL, 0)
            .setValue(TIME, 0)
        );
    }
}
