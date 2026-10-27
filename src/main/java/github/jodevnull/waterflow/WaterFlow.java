package github.jodevnull.waterflow;

import com.mojang.logging.LogUtils;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.content.block.WaterChannelBlock;
import github.jodevnull.waterflow.event.ModPlayerEvents;
import github.jodevnull.waterflow.event.ModServerEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WaterFlow.MODID)
public class WaterFlow
{
    public static final String MODID = "waterflow";
    public static final Logger logger = LogUtils.getLogger();

    public static final TagKey<Block> WATER_CHANNEL_TARGET = tagBlock("water_channel_target");
    public static final TagKey<Block> NEEDS_WATER_SOURCE = tagBlock("needs_water_source");

    public WaterFlow(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, Config.Spec);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModPlayerEvents.register();
        ModServerEvents.register();

        modEventBus.addListener(this::onSetup);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        WaterChannelBlock.BlockMap.init();
    }

    public static TagKey<Block> tagBlock(String name) {
        return TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID, name)
        );
    }
}
