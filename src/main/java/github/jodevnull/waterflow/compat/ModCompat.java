package github.jodevnull.waterflow.compat;

import github.jodevnull.waterflow.WaterFlow;
import net.minecraftforge.fml.ModList;

public interface ModCompat
{
    String location();
    String modName();

    default boolean isLoaded() {
        return false;
    }

    static <T extends ModCompat> T tryLoading(T compat) {
        if (ModList.get().isLoaded(compat.modName())) {
            try {
                compat = (T) Class.forName(compat.location()).newInstance();
            } catch (Exception e) {
                WaterFlow.logger.error("Error loading {} compat class: {}", compat.modName(), e);
            } finally {
                if (compat.isLoaded()) {
                    WaterFlow.logger.info("Loaded {} compat", compat.modName());
                }
            }
        }

        return compat;
    }
}
