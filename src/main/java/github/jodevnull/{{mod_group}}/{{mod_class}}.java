package github.jodevnull.{{mod_group}};

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod({{mod_class}}.MODID)
public class {{mod_class}}
{
    public static final String MODID = "{{mod_id}}";
    public static final Logger LOGGER = LogUtils.getLogger();

    public {{mod_class}}(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup");
    }
}
