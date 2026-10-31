package github.jodevnull.waterflow.datagen;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.datagen.loot.ModLootTableProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WaterFlow.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerator
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        final var generator = event.getGenerator();
        final var packOutput = generator.getPackOutput();
        final var fileHelper = event.getExistingFileHelper();
        final var lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, fileHelper));

        final var blockTagProvider = generator.addProvider(event.includeServer(),
            new ModBlockTagProvider(packOutput, lookupProvider, fileHelper)
        );

        generator.addProvider(event.includeServer(), new ModItemTagProvider(
            packOutput, lookupProvider, blockTagProvider.contentsGetter(), fileHelper)
        );
    }
}
