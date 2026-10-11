package github.jodevnull.{{mod_group}};

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod({{mod_class}}.MODID)
public class {{mod_class}}
{
    public static final String MODID = "{{mod_id}}";
    public static final Logger LOGGER = LogUtils.getLogger();

    public {{mod_class}}(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup");
    }
}
