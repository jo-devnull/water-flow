package github.jodevnull.waterflow.event;

import de.leximon.fluidlogged.Fluidlogged;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.block.WaterChannelBlock;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.WaterSystem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModPlayerEvents
{
    public static void register() {}

    @SubscribeEvent
    public static void onUsePickaxe(PlayerInteractEvent.RightClickBlock event) {
        final var level = event.getLevel();
        final var player = event.getEntity();
        final var blockHit = event.getHitVec();
        final var hand = event.getHand();
        final var item = player.getItemInHand(hand);

        if (item.is(ItemTags.PICKAXES)) {
            final BlockState state = level.getBlockState(blockHit.getBlockPos());
            final SoundEvent sound = state.getSoundType().getPlaceSound();

            if (state.is(WaterFlow.WATER_CHANNEL_TARGET)) {
                if (WaterChannelBlock.handleInteraction(level, player, hand, item, blockHit)) {
                    player.swing(hand);
                    level.playSound(null, blockHit.getBlockPos(), sound, SoundSource.BLOCKS);
                }
            }
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
