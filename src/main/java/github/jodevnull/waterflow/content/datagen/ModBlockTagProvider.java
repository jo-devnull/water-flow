package github.jodevnull.waterflow.content.datagen;

import github.jodevnull.waterflow.WFBlocks;
import github.jodevnull.waterflow.WFTags;
import github.jodevnull.waterflow.WaterFlow;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
public class ModBlockTagProvider extends BlockTagsProvider
{
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WaterFlow.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        for (final var block : WFBlocks.CHANNELS.list()) {
            this.tag(WFTags.Blocks.WATER_CHANNELS).add(block.get());
        }

        this.tag(WFTags.Blocks.EARTHY_CHANNELS)
            .add(WFBlocks.DIRT_CHANNEL.get())
            .add(WFBlocks.SAND_CHANNEL.get());

        this.tag(WFTags.Blocks.NEEDS_WATER_SOURCE)
            .add(Blocks.KELP)
            .add(Blocks.KELP_PLANT)
            .add(Blocks.TALL_SEAGRASS)
            .add(Blocks.SEAGRASS)
            .add(Blocks.BUBBLE_COLUMN)
            .addOptionalTag(BlockTags.UNDERWATER_BONEMEALS);
    }
}
