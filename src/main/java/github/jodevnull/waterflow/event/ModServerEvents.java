package github.jodevnull.waterflow.event;

import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.core.water.WaterManager;
import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModServerEvents
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
                    world.setBlockAndUpdate(rPos, Blocks.AIR.defaultBlockState());
                    WaterSystem.waterEvaporationEffect(world, rPos);
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
