package github.jodevnull.waterflow;

import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.utils.RegistryList;
import github.jodevnull.waterflow.utils.Utils;
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
    public static final RegistryObject<ChannelBlock> SAND_CHANNEL;

    static {
        SAND_CHANNEL = addChannelBlock(Blocks.SAND);
        DIRT_CHANNEL = addChannelBlock(Blocks.DIRT);

        addChannelBlock(Blocks.STONE);
        addChannelBlock(Blocks.ANDESITE);
        addChannelBlock(Blocks.SANDSTONE);
        addChannelBlock(Blocks.COBBLESTONE);
        addChannelBlock(Blocks.SMOOTH_STONE);
        addChannelBlock(Blocks.COPPER_BLOCK);
        addChannelBlock(Blocks.CUT_COPPER);
    }

    private static RegistryObject<ChannelBlock> addChannelBlock(Block parent) {
        final String name = Utils.blockName(parent) + "_channel";
        return CHANNELS.add(name, () -> new ChannelBlock(parent));
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
