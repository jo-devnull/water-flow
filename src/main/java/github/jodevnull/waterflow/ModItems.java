package github.jodevnull.waterflow;

import github.jodevnull.waterflow.utils.RegistryList;
import github.jodevnull.waterflow.utils.Utils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, WaterFlow.MODID);

    public static RegistryList<Item, BlockItem> CHANNELS = new RegistryList<>(ITEMS);

    static {
        for (final var block : ModBlocks.CHANNELS.list()) {
            CHANNELS.add(Utils.nameof(block), () -> new BlockItem(block.get(), new Item.Properties()));
        }
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
