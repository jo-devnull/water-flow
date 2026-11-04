package github.jodevnull.waterflow.recipe;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.jodevnull.waterflow.registry.ModRecipeSerializers;
import github.jodevnull.waterflow.registry.ModRecipeTypes;
import github.jodevnull.waterflow.WaterFlow;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
public class WaterInteractionRecipe implements Recipe<SimpleContainer>
{
    public static String NAME = "water_interaction";

    private final ResourceLocation id;
    private final String group;
    private final int amount;
    private final Ingredient input;
    private final ItemStack output;
    private final boolean consume;

    public WaterInteractionRecipe(ResourceLocation id, String group, int amount, Ingredient input, ItemStack output, boolean consume) {
        this.id = id;
        this.group = group;
        this.amount = amount;
        this.input = input;
        this.output = output;
        this.consume = consume;
    }

    public Ingredient getInput() {
        return this.input;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.input);
        return nonnulllist;
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public ItemStack assemble(@NotNull SimpleContainer inv, @NotNull RegistryAccess access) {
        return this.output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(@NotNull RegistryAccess access) {
        return output;
    }

    @Override
    public boolean matches(SimpleContainer inv, @NotNull Level level) {
        if (inv.isEmpty())
            return false;

        return input.test(inv.getItem(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.WATER_INTERACTION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.WATER_INTERACTION.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WaterInteractionRecipe that = (WaterInteractionRecipe) o;

        if (!getId().equals(that.getId())) return false;
        if (!getGroup().equals(that.getGroup())) return false;
        if (!input.equals(that.input)) return false;
        if (amount != that.getAmount()) return false;

        return this.output.equals(that.getOutput());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getGroup().hashCode();
        result = 31 * result + input.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + ((Object) amount).hashCode();
        return result;
    }

    @ParametersAreNonnullByDefault
    public static class Serializer implements RecipeSerializer<WaterInteractionRecipe>
    {
        public Serializer() {}

        @Override
        public WaterInteractionRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            final var group = GsonHelper.getAsString(json, "group", "");
            final var amount = GsonHelper.getAsInt(json, "amount");
            final var container = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "container"));
            final var consume = GsonHelper.getAsBoolean(json, "consume", true);
            final var result = readOutput(GsonHelper.getAsJsonObject(json, "result"));

            if (container.isEmpty())
                throw new JsonParseException("No ingredients for %s recipe".formatted(NAME));

            if (result.isEmpty())
                throw new JsonParseException("No output for %s recipe".formatted(NAME));

            if (amount <= 0)
                throw new JsonParseException("recipe %s: amount must be a positive non-zero integer!".formatted(NAME));

            return new WaterInteractionRecipe(recipeId, group, amount, container, result, consume);
        }

        @Nullable
        @Override
        public WaterInteractionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String group = buffer.readUtf(32767);
            int amount = buffer.readInt();
            boolean consume = buffer.readBoolean();
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();

            return new WaterInteractionRecipe(recipeId, group, amount, input, output, consume);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WaterInteractionRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeInt(recipe.amount);
            buffer.writeBoolean(recipe.consume);
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }

        private static ItemStack readOutput(JsonElement element) {
            if (!element.isJsonObject())
                throw new JsonSyntaxException("Must be a json object");

            final var json = element.getAsJsonObject();
            final var itemId = GsonHelper.getAsString(json, "item");

            final ItemStack itemstack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemId))), 1);

            if (json.has("nbt")) {
                try {
                    final var nbt = json.get("nbt");
                    itemstack.setTag(TagParser.parseTag(
                        nbt.isJsonObject() ? WaterFlow.GSON.toJson(nbt) : GsonHelper.convertToString(nbt, "nbt")));
                }
                catch (CommandSyntaxException e) {
                    WaterFlow.logger.error("Failed to parse item nbt in recipe:", e);
                }
            }

            return itemstack;
        }
    }
}