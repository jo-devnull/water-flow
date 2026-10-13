package github.jodevnull.waterflow.mixin;

import github.jodevnull.waterflow.core.water.WaterManager;
import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @Redirect(method = "tick", at= @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/ticks/LevelTicks;tick(JILjava/util/function/BiConsumer;)V"))
    private void tickManagers(LevelTicks<Fluid> instance, long pGameTime, int pMaxAllowedTicks, BiConsumer<BlockPos, Fluid> pTicker) {
        final ServerLevel world = (ServerLevel) (Object) this;

        if (WaterSystem.isLoaded(world)) {
            WaterSystem.of(world).ifPresent(WaterManager::beforeTick);
        }

        instance.tick(pGameTime, pMaxAllowedTicks, pTicker);

        if (WaterSystem.isLoaded(world)) {
            WaterSystem.of(world).ifPresent(WaterManager::afterTick);
        }
    }
}
