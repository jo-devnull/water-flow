package github.jodevnull.waterflow.datagen.loot;

import github.jodevnull.waterflow.ModBlocks;
import github.jodevnull.waterflow.ModTags;
import github.jodevnull.waterflow.block.ChannelBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class ModBlockLootTables extends BlockLootSubProvider
{
    protected ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        for (final var block : ModBlocks.CHANNELS.list()) {
            dropOther(block.get(), getDropFor(block));
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }

    public Block getDropFor(Supplier<ChannelBlock> channel) {
        // earthy channels (dirt, sand, gravel, etc...) will drop their parent
        if (channel.get().defaultBlockState().is(ModTags.Blocks.EARTHY_CHANNELS)) {
            return channel.get().getParent();
        }

        // other channels (stone or custom made) will drop itself
        return channel.get();
    }
}
