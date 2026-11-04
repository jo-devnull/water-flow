package github.jodevnull.waterflow.event;

import github.jodevnull.waterflow.registry.ModRecipeTypes;
import github.jodevnull.waterflow.core.WaterSystem;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class RecipeEventHandler
{
    @SubscribeEvent
    public static void onWaterInteraction(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getSide().isClient())
            return;

        final var world = event.getLevel();
        final var player = event.getEntity();
        final var hand = event.getHand();
        final var pos = event.getPos().relative(event.getHitVec().getDirection());

        if (player.getItemInHand(hand).isEmpty() || !FluidUtils.isFiniteWater(world, pos)) {
            return;
        }

        if (handleWaterInteraction((ServerLevel) event.getLevel(), player, hand, pos)) {
            event.setCanceled(true);
        }
    }

    public static boolean handleWaterInteraction(ServerLevel level, Player player, InteractionHand hand, BlockPos pos) {
        final var input = player.getItemInHand(hand);
        final var container = new SimpleContainer(input);

        final var recipeOpt =
            level.getRecipeManager().getRecipeFor(ModRecipeTypes.WATER_INTERACTION.get(), container, level);

        if (recipeOpt.isEmpty())
            return false;

        final var recipe = recipeOpt.get();
        final int minWaterRequired = recipe.getAmount();
        final int waterLevel = WaterSystem.getWaterLevel(level, pos).orElse(-1);

        if (waterLevel < minWaterRequired)
            return false;

        WaterSystem.setWaterLevel(level, pos, waterLevel - minWaterRequired);
        player.getItemInHand(hand).shrink(1);
        player.getInventory().add(recipe.getOutput().copy());

        return true;
    }
}