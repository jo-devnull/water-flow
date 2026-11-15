package github.jodevnull.waterflow.event;

import github.jodevnull.waterflow.compat.ModCompat;
import github.jodevnull.waterflow.core.IWaterSystem;
import github.jodevnull.waterflow.registry.ModTags;
import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.utils.WaterEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModPlayerEvents
{
    public static void register() {}

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        IWaterSystem.get((ServerLevel) event.getLevel())
            .map(IWaterSystem::getFlowMap)
            .ifPresent(flowMap -> flowMap.updateFlowAt(event.getPos()));
    }

    @SubscribeEvent
    public static void onFlattenBlock(BlockEvent.BlockToolModificationEvent event) {
        final var player = event.getPlayer();

        if (event.isSimulated()) {
            return;
        }

        if (event.getToolAction() == ToolActions.SHOVEL_FLATTEN) {
            final var blockState = event.getFinalState();
            final var output = ChannelBlock.BlockMap.get(blockState);
            final var dir = player == null ? Direction.NORTH : player.getDirection();

            if (output != null) {
                event.setFinalState(output.setValue(ChannelBlock.FACING, dir));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerPushWater(PlayerInteractEvent.RightClickBlock event) {
        final var level = event.getLevel();
        final var player = event.getEntity();
        final var facing = player.getDirection();
        final var blockPos = event.getHitVec().getBlockPos();

        if (player.getItemInHand(event.getHand()).is(ModTags.Items.CAN_PUSH_WATER)) {
            final var pos = ModCompat.Fluidlogged.isFluidloggable(level.getBlockState(blockPos))
                ? blockPos : blockPos.relative(event.getHitVec().getDirection());

            final var dir = player.isSteppingCarefully() ? facing.getOpposite() : facing;
            final var fluidState = FluidUtils.getFluidState(level, pos);

            if (FluidUtils.isFiniteWater(fluidState)) {
                if (level instanceof ServerLevel world) {
                    IWaterSystem.get(world).ifPresent(system -> {
                        tryPushWater(system, pos, dir, fluidState.getAmount());
                        system.getFlowMap().updateNeighbors(pos);
                    });

                    WaterEffects.splash(world, pos);
                }

                player.swing(event.getHand());
            }
        }
    }

    public static void tryPushWater(IWaterSystem manager, BlockPos pos, Direction dir, int amount) {
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
