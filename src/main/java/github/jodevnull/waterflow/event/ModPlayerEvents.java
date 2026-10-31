package github.jodevnull.waterflow.event;

import github.jodevnull.waterflow.ModTags;
import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.compat.Compat;
import github.jodevnull.waterflow.core.WaterManager;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.WaterSystem;
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
