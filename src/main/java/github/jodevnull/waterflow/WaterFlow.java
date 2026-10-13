package github.jodevnull.waterflow;

import com.mojang.logging.LogUtils;
import github.jodevnull.waterflow.event.ModLevelEvents;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.*;

@Mod(WaterFlow.MODID)
public class WaterFlow
{
    public static final String MODID = "waterflow";
    public static final Logger logger = LogUtils.getLogger();

    public WaterFlow(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        ModLevelEvents.register();
    }
}
