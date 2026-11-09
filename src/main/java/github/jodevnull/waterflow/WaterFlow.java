package github.jodevnull.waterflow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.compat.ModCompat;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.event.ModPlayerEvents;
import github.jodevnull.waterflow.event.ModServerEvents;
import github.jodevnull.waterflow.registry.*;
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

    // TODO: implement cloth for absorving small amounts of water
    // TODO: implement pipes
    // TODO: add support for create water wheels

    public WaterFlow(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, Config.Spec);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);
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
