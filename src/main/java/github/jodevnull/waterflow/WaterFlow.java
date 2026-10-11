package github.jodevnull.waterflow;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WaterFlow.MODID)
public class WaterFlow
{
    public static final String MODID = "waterflow";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WaterFlow(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup");
    }
}
