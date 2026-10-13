package github.jodevnull.waterflow.config;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

public final class Config
{
    public static final List<String> enabledDimensions = List.of(
        "minecraft:overworld",
        "minecraft:the_end"
    );

    public static boolean isEnabled(ResourceKey<Level> dimension) {
        return enabledDimensions.contains(dimension.location().toString());
    }

    public static boolean isEnabled(Level world) {
        return isEnabled(world.dimension());
    }
}
