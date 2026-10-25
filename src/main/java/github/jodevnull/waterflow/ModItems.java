package github.jodevnull.waterflow;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems
{
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WaterFlow.MODID);

    public static final RegistryObject<Item> DIRT_CHANNEL =
        ITEMS.register("dirt_channel", () -> new BlockItem(ModBlocks.DIRT_CHANNEL.get(), new Item.Properties()));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
