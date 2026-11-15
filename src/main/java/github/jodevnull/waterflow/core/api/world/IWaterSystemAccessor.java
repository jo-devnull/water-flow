package github.jodevnull.waterflow.core.api.world;

import github.jodevnull.waterflow.core.IWaterSystem;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface IWaterSystemAccessor
{
    IWaterSystem wf_getWaterSystem();

    @Nullable
    static IWaterSystem get(ServerLevel world) {
        return ((IWaterSystemAccessor) world).wf_getWaterSystem();
    }
}
