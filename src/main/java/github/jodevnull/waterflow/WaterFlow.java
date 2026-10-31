package github.jodevnull.waterflow;

import com.mojang.logging.LogUtils;
import github.jodevnull.waterflow.block.ChannelBlock;
import github.jodevnull.waterflow.compat.Compat;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.event.ModPlayerEvents;
import github.jodevnull.waterflow.event.ModServerEvents;
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

    // TODO: implement cloth for absorving small amounts of water
    // TODO: implement recipe for filling containers with water

    public WaterFlow(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, Config.Spec);

        Compat.init();
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModPlayerEvents.register();
        ModServerEvents.register();

        modEventBus.addListener(this::onSetup);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        ChannelBlock.BlockMap.init();
    }

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
