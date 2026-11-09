package github.jodevnull.waterflow.compat;

import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.compat.create.CreateCompat;
import github.jodevnull.waterflow.compat.fluidlogged.FluidLoggedCompat;
import net.minecraftforge.fml.ModList;

public final class ModCompat
{
    public static CreateCompat Create = new CreateCompat() {};
    public static FluidLoggedCompat Fluidlogged = new FluidLoggedCompat() {};

    static void init() {
        Create = tryLoadImpl("create", "github.jodevnull.waterflow.compat.create.CreateIpl", Create);
        Fluidlogged = tryLoadImpl("fluidlogged", "github.jodevnull.waterflow.compat.fluidlogged.FluidloggedImpl", Fluidlogged);
    }

    private static <T> T tryLoadImpl(String modName, String className, T compat) {
        if (ModList.get().isLoaded(modName)) {
            try {
                compat = (T) Class.forName(className).newInstance();
            } catch (Exception e) {
                WaterFlow.logger.error("Error loading {} compat class: {}", modName, e);
            } finally {
                WaterFlow.logger.info("Loaded {} compat", modName);
            }
        }

        return compat;
    }
}
