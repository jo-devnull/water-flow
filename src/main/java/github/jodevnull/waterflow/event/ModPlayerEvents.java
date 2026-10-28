package github.jodevnull.waterflow.event;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.block.WaterChannelBlock;
import github.jodevnull.waterflow.compat.Compat;
import github.jodevnull.waterflow.core.WaterManager;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.WaterSystem;
import github.jodevnull.waterflow.utils.WaterEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        final var facing = player.getDirection();
        final var blockPos = event.getHitVec().getBlockPos();

        if (player.getItemInHand(event.getHand()).is(WaterFlow.CAN_PUSH_WATER)) {
            final var pos = Compat.Fluidlogged.isFluidloggable(level.getBlockState(blockPos))
                ? blockPos : blockPos.relative(event.getHitVec().getDirection());

            final var dir = player.isSteppingCarefully() ? facing.getOpposite() : facing;
            final var fluidState = FluidUtils.getFluidState(level, pos);

            if (FluidUtils.isFiniteWater(fluidState)) {
                WaterSystem.of(level).ifPresent(manager -> tryPushWater(manager, pos, dir, fluidState.getAmount()));

                if (level instanceof ServerLevel world) {
                    WaterEffects.pushWater(world, pos);
                }

                player.swing(event.getHand());
            }
        }
    }

    public static void tryPushWater(WaterManager manager, BlockPos pos, Direction dir, int amount) {
        int nextLevel = -1, count = 0;

        while (count < 4) {
            nextLevel = manager.getWaterLevel(pos.relative(dir));

            if (nextLevel > -1) {
                break;
            } else {
                dir = dir.getClockWise(); count++;
            }
        }

        if (nextLevel < 0) {
            dir = Direction.UP;
        }

        manager.setWater(pos, 0);
        manager.addWater(pos.relative(dir), amount, dir);
    }
}
