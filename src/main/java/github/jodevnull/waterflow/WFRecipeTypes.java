package github.jodevnull.waterflow;

import github.jodevnull.waterflow.content.recipe.WaterInteractionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WFRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, WaterFlow.MODID);

    public static final Supplier<RecipeType<WaterInteractionRecipe>> WATER_INTERACTION =
        RECIPE_TYPES.register(WaterInteractionRecipe.NAME,
            () -> new RecipeType<>() {
                public String toString() {
                    return WaterFlow.MODID + ":" + WaterInteractionRecipe.NAME;
                }
            }
        );

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}