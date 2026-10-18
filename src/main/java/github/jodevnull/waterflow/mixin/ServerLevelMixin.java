package github.jodevnull.waterflow.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.water.WaterManager;
import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.ticks.LevelTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level
{
    protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

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

    @Unique
    private int m_tickCounter = 0;

    @Inject(method = "tickChunk", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V"))
    private void ifc$spawnRainWater(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci, @Local(name="blockpos1") BlockPos blockpos) {
        // if (!MercuryConfig.rainFlooding())
        //     return;

        m_tickCounter++;

        if (m_tickCounter > 10 /*MercuryConfig.rainFloodingInterval()*/)
            m_tickCounter = 0;
        else return;

        if (!isRainingAt(blockpos)) {
            return;
        }

        final var world = (ServerLevel) (Object) this;
        final var state = getBlockState(blockpos);
        final var below = getBlockState(blockpos.below());
        final var fluid = FluidUtils.getFluidState(this, blockpos.below());

        if (!fluid.isEmpty() || below.is(Blocks.SNOW))
            return;

        if (state.is(Blocks.AIR)) {
            world.setBlockAndUpdate(blockpos, FluidUtils
                .getWater(2)
                .createLegacyBlock()
                .setValue(FluidProperty.TIME, FluidProperty.MAX_TIME)
            );
        }
    }
}
