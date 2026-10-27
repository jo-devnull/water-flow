package github.jodevnull.waterflow.mixin.compat.fluidlogged;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @Redirect(method = "tickChunk", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState tickFluidlogged(BlockState instance, @Local(name = "levelchunksection") LevelChunkSection levelChunkSection, @Local(name="blockpos3") BlockPos pos, @Local(name="i") int i, @Local(name="k1") int k1, @Local(name="j") int j) {
        return levelChunkSection.getFluidState(pos.getX() - i, pos.getY() - k1, pos.getZ() - j);
    }
}
