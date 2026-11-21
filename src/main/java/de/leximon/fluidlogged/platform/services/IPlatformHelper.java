package de.leximon.fluidlogged.platform.services;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.network.forge.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.forge.ClientboundSectionFluidsUpdatePacket;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public final class IPlatformHelper
{
    private static final PacketDistributor<List<ServerPlayer>> PLAYER_LIST_DISTRIBUTOR =
        new PacketDistributor<>((distributor, supplier) -> packet ->
            supplier.get().forEach(player -> player.connection.send(packet)),
            NetworkDirection.PLAY_TO_CLIENT
        );

    public IdMapper<FluidState> getFluidStateIdMapper() {
        return Fluidlogged.fluidStateIdMapper;
    }

    public void broadcastFluidUpdatePacket(List<ServerPlayer> players, BlockPos pos, FluidState state) {
        Fluidlogged.PACKET_CHANNEL.send(PLAYER_LIST_DISTRIBUTOR.with(() -> players), new ClientboundFluidUpdatePacket(pos, state));
    }

    public void broadcastSectionFluidsUpdatePacket(List<ServerPlayer> players, SectionPos sectionPos, ShortSet changedFluids, LevelChunkSection levelChunkSection) {
        Fluidlogged.PACKET_CHANNEL.send(PLAYER_LIST_DISTRIBUTOR.with(() -> players), new ClientboundSectionFluidsUpdatePacket(sectionPos, changedFluids, levelChunkSection));
    }
}
