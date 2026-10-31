package github.jodevnull.waterflow;

import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.utils.Utils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, WaterFlow.MODID);

    public static final RegistryObject<Item> DIRT_CHANNEL;
    public static final RegistryObject<Item> STONE_CHANNEL;
    public static final RegistryObject<Item> SAND_CHANNEL;
    public static final RegistryObject<Item> ANDESITE_CHANNEL;

    static {
        DIRT_CHANNEL     = channelBlockItem(ModBlocks.DIRT_CHANNEL);
        STONE_CHANNEL    = channelBlockItem(ModBlocks.STONE_CHANNEL);
        SAND_CHANNEL     = channelBlockItem(ModBlocks.SAND_CHANNEL);
        ANDESITE_CHANNEL = channelBlockItem(ModBlocks.ANDESITE_CHANNEL);
    }

    private static RegistryObject<Item> channelBlockItem(RegistryObject<ChannelBlock> block) {
        return ITEMS.register(Utils.nameof(block), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
