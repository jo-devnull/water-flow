package github.jodevnull.waterflow.utils;

import github.jodevnull.waterflow.WaterFlow;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Objects;

public final class Utils
{
    public static <T> String nameof(RegistryObject<T> object) {
        assert object.getId() != null;
        return object.getId().getPath();
    }

    public static String blockName(Block block) {
        // key should never be null
        final var key = ForgeRegistries.BLOCKS.getKey(block);
        return Objects.requireNonNull(key).getPath();
    }

    @Nullable
    public static Block vanillaBlock(String name) {
        return ForgeRegistries.BLOCKS.getValue(
            ResourceLocation.fromNamespaceAndPath("minecraft", name)
        );
    }

    public static TagKey<Block> tagBlock(String name) {
        return TagKey.create(Registries.BLOCK, WaterFlow.res(name));
    }

    public static TagKey<Item> tagItem(String name) {
        return TagKey.create(Registries.ITEM, WaterFlow.res(name));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
