package github.jodevnull.waterflow;

import com.mojang.logging.LogUtils;
import github.jodevnull.waterflow.config.Config;
import github.jodevnull.waterflow.event.ModEventHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(WaterFlow.MODID)
public class WaterFlow
{
    public static final String MODID = "waterflow";
    public static final Logger logger = LogUtils.getLogger();

    public static final TagKey<Block> CAN_BLOCK_FLOW = TagKey.create(
        Registries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(MODID, "can_block_flow")
    );

    public WaterFlow(FMLJavaModLoadingContext context) {
        final var modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, Config.Spec);
        ModEventHandler.register();
    }
}
