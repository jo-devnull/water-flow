package de.leximon.fluidlogged;

import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.network.forge.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.forge.ClientboundSectionFluidsUpdatePacket;
import de.leximon.fluidlogged.platform.services.Services;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Fluidlogged
{
    public static final int UPDATE_SCHEDULE_FLUID_TICK = 0x80;
    public static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(
        WaterFlow.res("fluidlogged_channel"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static IdMapper<FluidState> fluidStateIdMapper;

    public static int getFluidId(@Nullable FluidState fluidState) {
        if (fluidState == null) {
            return 0;
        } else {
            int i = Services.PLATFORM.getFluidStateIdMapper().getId(fluidState);
            return i == -1 ? 0 : i;
        }
    }

    public static boolean canPlaceFluid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return Config.isFluidloggable(blockState);
    }

    public static boolean isFluidloggable(BlockState blockState) {
        return Config.isFluidloggable(blockState);
    }

    public static void loadComplete(FMLLoadCompleteEvent event) {
        IdMapper<FluidState> idMapper = new IdMapper<>();

        for (Fluid fluid : ForgeRegistries.FLUIDS) {
            for(FluidState fluidstate : fluid.getStateDefinition().getPossibleStates()) {
                idMapper.add(fluidstate);
            }
        }

        fluidStateIdMapper = idMapper;
    }

    public static void registerPackets() {
        PACKET_CHANNEL.registerMessage(
            0, ClientboundFluidUpdatePacket.class,
            ClientboundFluidUpdatePacket::write, ClientboundFluidUpdatePacket::read,
            ClientboundFluidUpdatePacket::apply,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        PACKET_CHANNEL.registerMessage(
            1, ClientboundSectionFluidsUpdatePacket.class,
            ClientboundSectionFluidsUpdatePacket::write, ClientboundSectionFluidsUpdatePacket::read,
            ClientboundSectionFluidsUpdatePacket::apply,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    @ApiStatus.Internal
    public static class Internal {
        public static boolean hasDifferentLightEmission(FluidState prevFluidState, FluidState newFluidState) {
            return prevFluidState.createLegacyBlock().getLightEmission() != newFluidState.createLegacyBlock().getLightEmission();
        }

        public static BlockState handleBlockRemoval(Level instance, BlockPos blockPos, int flags, int maxUpdateDepth) {
            final var fluidState = FluidUtils.getFluidState(instance, blockPos);
            ((LevelExtension) instance).setFluid(blockPos, Fluids.EMPTY.defaultFluidState(), flags, maxUpdateDepth);

            int amount = Mth.clamp(8 - fluidState.getAmount(), 0, 8);

            return fluidState.createLegacyBlock()
                .trySetValue(LiquidBlock.LEVEL, amount)
                .trySetValue(FluidProperty.TIME, Config.getEvaporationTime());
        }
    }
}
