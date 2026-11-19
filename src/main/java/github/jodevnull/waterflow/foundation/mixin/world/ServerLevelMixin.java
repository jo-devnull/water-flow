package github.jodevnull.waterflow.foundation.mixin.world;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.waterflow.foundation.config.Config;
import github.jodevnull.waterflow.foundation.core.api.IWaterSystem;
import github.jodevnull.waterflow.foundation.core.WaterSystem;
import github.jodevnull.waterflow.foundation.core.api.world.IWaterSystemAccessor;
import github.jodevnull.waterflow.foundation.core.feat.CommonFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.ticks.LevelTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements IWaterSystemAccessor
{
    @Unique
    private IWaterSystem wf_system;

    @Override
    public IWaterSystem wf_getWaterSystem() {
        return wf_system;
    }

    protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at=@At("TAIL"))
    private void onInit(MinecraftServer pServer, Executor pDispatcher, LevelStorageSource.LevelStorageAccess pLevelStorageAccess, ServerLevelData pServerLevelData, ResourceKey pDimension, LevelStem pLevelStem, ChunkProgressListener pProgressListener, boolean pIsDebug, long pBiomeZoomSeed, List pCustomSpawners, boolean pTickTime, RandomSequences pRandomSequences, CallbackInfo ci) {
        final ServerLevel self = (ServerLevel) (Object) this;

        if (Config.isEnabled(self)) {
            this.wf_system = new WaterSystem(self);
        } else {
            this.wf_system = null;
        }
    }

    @Redirect(method = "tick", at= @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/ticks/LevelTicks;tick(JILjava/util/function/BiConsumer;)V"))
    private void tickManagers(LevelTicks<Fluid> instance, long pGameTime, int pMaxAllowedTicks, BiConsumer<BlockPos, Fluid> pTicker) {
        if (wf_system != null)
            wf_system.beforeTick();

        instance.tick(pGameTime, pMaxAllowedTicks, pTicker);

        if (wf_system != null)
            wf_system.afterTick();
    }

    @Unique
    private int m_tickCounter = 0;

    @Inject(method = "tickChunk", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V"))
    private void doRainFlooding(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci, @Local(name="blockpos1") BlockPos blockpos) {
        final ServerLevel world = (ServerLevel) (Object) this;

        if (wf_system == null)
            return;

        if (!Config.rainWaterEnabled())
            return;

        final int interval = world.isThundering()
            ? Config.rainWaterInterval()
            : Config.rainFloodingInterval();

        m_tickCounter++;

        if (m_tickCounter > interval)
            m_tickCounter = 0;
        else return;

        CommonFeatures.rainFlooding(wf_system, blockpos);
    }
}
