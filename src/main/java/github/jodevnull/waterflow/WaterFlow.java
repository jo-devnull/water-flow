package github.jodevnull.waterflow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import github.jodevnull.waterflow.content.block.ChannelBlock;
import github.jodevnull.waterflow.foundation.compat.ModCompat;
import github.jodevnull.waterflow.foundation.config.Config;
import github.jodevnull.waterflow.content.event.ModPlayerEvents;
import github.jodevnull.waterflow.content.event.ModServerEvents;
import net.minecraft.resources.ResourceLocation;
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
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public WaterFlow(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, Config.Spec);

        WFBlocks.register(modEventBus);
        WFItems.register(modEventBus);
        WFCreativeTabs.register(modEventBus);
        WFRecipeTypes.register(modEventBus);
        WFRecipeSerializers.register(modEventBus);
        ModPlayerEvents.register();
        ModServerEvents.register();

        modEventBus.addListener(this::onSetup);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        ModCompat.init();
        ChannelBlock.BlockMap.init();
    }

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
