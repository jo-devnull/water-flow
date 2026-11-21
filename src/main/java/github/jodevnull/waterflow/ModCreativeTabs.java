package github.jodevnull.waterflow;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB, WaterFlow.MODID
    );

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + WaterFlow.MODID + ".main"))
            .icon(() -> new ItemStack(Items.WATER_BUCKET))
            .displayItems(ModCreativeTabs::displayItems)
            .build()
    );

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }

    private static void displayItems(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        for (final var item : ModItems.ITEMS.getEntries()) {
            pOutput.accept(item.get());
        }
    }
}
