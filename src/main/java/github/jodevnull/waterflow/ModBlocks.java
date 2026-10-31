package github.jodevnull.waterflow;

import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.utils.RegistryList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, WaterFlow.MODID);

    public static final RegistryList<Block, ChannelBlock> CHANNELS =
        new RegistryList<>(BLOCKS);

    public static final RegistryObject<ChannelBlock> DIRT_CHANNEL;
    public static final RegistryObject<ChannelBlock> STONE_CHANNEL;
    public static final RegistryObject<ChannelBlock> SAND_CHANNEL;
    public static final RegistryObject<ChannelBlock> ANDESITE_CHANNEL;

    static {
        DIRT_CHANNEL     = CHANNELS.add("dirt_channel",     () -> new ChannelBlock(Blocks.DIRT));
        STONE_CHANNEL    = CHANNELS.add("stone_channel",    () -> new ChannelBlock(Blocks.STONE));
        SAND_CHANNEL     = CHANNELS.add("sand_channel",     () -> new ChannelBlock(Blocks.SAND));
        ANDESITE_CHANNEL = CHANNELS.add("andesite_channel", () -> new ChannelBlock(Blocks.ANDESITE));
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
