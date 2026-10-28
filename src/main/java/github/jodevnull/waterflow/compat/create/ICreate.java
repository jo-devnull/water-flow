package github.jodevnull.waterflow.compat.create;

import net.minecraftforge.eventbus.api.IEventBus;

public interface ICreate
{
    default boolean isLoaded() {
        return false;
    }

    default void register(IEventBus eventBus) {}
}
