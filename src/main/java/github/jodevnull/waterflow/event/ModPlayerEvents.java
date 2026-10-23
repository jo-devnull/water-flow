package github.jodevnull.waterflow.event;

import de.leximon.fluidlogged.Fluidlogged;
import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.utils.Utils;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber
public class ModPlayerEvents
{
    public static void register() {}

    @SubscribeEvent
    public static void onShowDebugInfo(PlayerInteractEvent.RightClickBlock event) {
        final var level = event.getLevel();
        final var player = event.getEntity();
        final var blockPos = event.getHitVec().getBlockPos();
        final var hitDir = event.getHitVec().getDirection();
        final var blockState = level.getBlockState(blockPos);

        if (level.isClientSide()) {
            return;
        }

        if (player.getItemInHand(event.getHand()).is(Items.BLAZE_ROD)) {
            final var states = FlowHandler.getFlowState(blockState);
            final var message = new StringBuilder();

            message.append("# ");
            for (final var dir : Direction.values()) {
                message.append(dir.getName().toUpperCase(Locale.ROOT).charAt(0)).append(" ");
            }

            message.append("\n> ");
            for (final var dir : Direction.values()) {
                message.append(FlowHandler.checkFlowIn(states, dir) ? 1 : 0).append(" ");
            }

            message.append("\n< ");
            for (final var dir : Direction.values()) {
                message.append(FlowHandler.checkFlowOut(states, dir) ? 1 : 0).append(" ");
            }

            WaterSystem.of(level).ifPresent(manager -> {
                final var dir = player.getDirection();

                message
                    .append("\n")
                    .append("Can Flow = ")
                    .append(manager.canFlow(blockPos.relative(hitDir), dir));
            });

            player.sendSystemMessage(Component.literal(message.toString()));
        }

        else if (player.getItemInHand(event.getHand()).is(Items.STICK)) {
            WaterSystem.of(level).ifPresent(manager -> {
                final var pos = blockPos.relative(hitDir);
                final int waterLevel = manager.getWaterLevel(pos);
                manager.setWater(pos, Utils.clamp(waterLevel + 1, 0, 8));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerPushWater(PlayerInteractEvent.RightClickBlock event) {
        final var level = event.getLevel();
        final var player = event.getEntity();
        final var blockPos = event.getHitVec().getBlockPos();

        if (player.getItemInHand(event.getHand()).is(ItemTags.HOES)) {
            final var pos = Fluidlogged.isFluidloggable(level.getBlockState(blockPos))
                ? blockPos : blockPos.relative(event.getHitVec().getDirection());

            final var dir = player.getDirection();
            final var fluidState = FluidUtils.getFluidState(level, pos);

            if (FluidUtils.isFiniteWater(fluidState)) {
                WaterSystem.spread(level, pos, fluidState.getAmount(), dir);
                player.swing(event.getHand());

                if (level instanceof ServerLevel world) {
                    world.sendParticles(ParticleTypes.FALLING_WATER,
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        4 + world.random.nextInt(4), world.random.nextFloat(), 0.5D, world.random.nextFloat(), 0.05D
                    );

                    world.playSound(null, pos, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.PLAYERS,
                        0.8f + world.random.nextFloat() * 0.2f,
                        0.9f + world.random.nextFloat() * 0.2f
                    );
                }
            }
        }
    }
}
