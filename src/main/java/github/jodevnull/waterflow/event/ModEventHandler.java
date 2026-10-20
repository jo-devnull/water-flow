package github.jodevnull.waterflow.event;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.core.feature.EqualizeFeature;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModEventHandler
{
    public static void register() {}

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        WaterSystem.onUnload();
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel world) {
            WaterSystem.onUnload(world);
        }
    }

    @SubscribeEvent
    public static void onNeighborChange(BlockEvent.NeighborNotifyEvent event) {
        final var level = event.getLevel();

        if (level.isClientSide()) {
            return;
        }

        if (event.getState().is(WaterFlow.NEEDS_WATER_SOURCE)) {
            WaterSystem.run((Level) level, event.getPos());
        }

        for (final var dir : event.getNotifiedSides()) {
            final var pos = event.getPos().relative(dir);
            final var state = level.getBlockState(pos);

            if (state.is(WaterFlow.NEEDS_WATER_SOURCE)) {
                WaterSystem.run((Level) level, pos);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        final var level = event.getLevel();
        final var player = event.getEntity();
        final var targetPos = event.getHitVec().getBlockPos()
            .relative(event.getHitVec().getDirection());

        if (level.isClientSide()) {
            return;
        }

        if (player.getItemInHand(event.getHand()).is(Items.STICK)) {
            WaterSystem.of(level).ifPresent(manager -> {
                manager.setWater(targetPos, manager.getWaterLevel(targetPos) + 1);
            });
        }
    }

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {
        final var level = event.getLevel();
        final var fluid = FluidUtils.getFluidState(level, event.getLiquidPos());

        for (final var dir : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            final var rPos = event.getLiquidPos().relative(dir);
            final var rFluid = FluidUtils.getFluidState(level, rPos);

            if (rFluid.isEmpty() || !FluidProperty.isFinite(rFluid)) {
                continue;
            }

            if (level instanceof ServerLevel world) {
                if (fluid.is(FluidTags.LAVA)) {
                    WaterSystem.waterEvaporationEffect(world, rPos);
                    WaterSystem.of((Level) level).ifPresent(manager -> manager.setWater(rPos, 0));
                }

                if (rFluid.getAmount() <= 4) {
                    event.setNewState(fluid.getAmount() > 1
                        ? rFluid.setValue(FlowingFluid.LEVEL, fluid.getAmount() - 1).createLegacyBlock()
                        : Blocks.AIR.defaultBlockState()
                    );
                }
            }
        }
    }
}
