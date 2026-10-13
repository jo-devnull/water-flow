package github.jodevnull.waterflow.event;

import github.jodevnull.waterflow.core.water.WaterManager;
import github.jodevnull.waterflow.core.water.WaterSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class ModLevelEvents
{
    public static void register() {}

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel world) {
            WaterSystem.of(world).ifPresent(WaterManager::unload);
        }
    }
}
