package github.jodevnull.waterflow.registry;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.recipe.WaterInteractionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, WaterFlow.MODID);

    public static final Supplier<RecipeSerializer<?>> WATER_INTERACTION =
        RECIPE_SERIALIZERS.register(WaterInteractionRecipe.NAME, WaterInteractionRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}