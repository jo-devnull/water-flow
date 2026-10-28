package github.jodevnull.waterflow;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WaterFlow.MODID);

    public static final RegistryObject<Item> DIRT_CHANNEL     = channel("dirt_channel", ModBlocks.DIRT_CHANNEL);
    public static final RegistryObject<Item> STONE_CHANNEL    = channel("stone_channel", ModBlocks.STONE_CHANNEL);
    public static final RegistryObject<Item> SAND_CHANNEL     = channel("sand_channel", ModBlocks.SAND_CHANNEL);
    public static final RegistryObject<Item> ANDESITE_CHANNEL = channel("andesite_channel", ModBlocks.ANDESITE_CHANNEL);

    private static RegistryObject<Item> channel(String name, Supplier<Block> blockSupplier) {
        return ITEMS.register(name, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
