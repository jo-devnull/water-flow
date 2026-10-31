package github.jodevnull.waterflow.datagen;

import github.jodevnull.waterflow.ModBlocks;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.utils.Utils;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class ModBlockStateProvider extends BlockStateProvider
{
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WaterFlow.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerChannels();
        registerWetWool();
    }

    private void registerChannels() {
        for (final var block : ModBlocks.CHANNELS.list()) {
            final var name = Utils.nameof(block);
            final var texture = name.replace("_channel", "");

            horizontalBlock(block.get(), models()
                .withExistingParent(name, modLoc("block/water_channel"))
                .texture("base", mcLoc("block/" + texture))
                .texture("top", mcLoc("block/" + texture))
            );

            simpleBlockItem(block.get(), models().getBuilder(name));
        }
    }

    private void registerWetWool() {
        for (final var block : ModBlocks.WET_WOOL.list()) {
            final var name = Utils.nameof(block);
            final var texture = "block/wet_wool/" + name.replace("_wet_wool", "");

            simpleBlockWithItem(block.get(), models().cubeAll(name, modLoc(texture)));
        }
    }
}
