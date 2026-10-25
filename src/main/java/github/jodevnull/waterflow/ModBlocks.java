package github.jodevnull.waterflow;

import github.jodevnull.waterflow.content.block.WaterChannelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WaterFlow.MODID);

    public static final RegistryObject<Block> DIRT_CHANNEL = channelBlock("dirt_channel", Blocks.DIRT);
    public static final RegistryObject<Block> STONE_CHANNEL = channelBlock("stone_channel", Blocks.STONE);

    private static RegistryObject<Block> channelBlock(String name, Block parent) {
        return BLOCKS.register(name, () -> new WaterChannelBlock(parent));
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
