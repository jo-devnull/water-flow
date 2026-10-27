package github.jodevnull.waterflow.config;

import com.google.common.collect.ImmutableList;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.utils.Utils;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Config
{
    private static final ForgeConfigSpec.Builder Builder = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue m_waterEvaporation = Builder
        .comment("-------------------------------------------------------------------------------------------")
        .comment(" If true, small pockets of water will slowly evaporate over time.")
        .comment(" This does not affect full blocks of water.")
        .define("waterEvaporation", true);

    private static final ForgeConfigSpec.IntValue m_waterEvaporationTime = Builder
        .comment("--------------------------------------------------------------------------------------------")
        .comment("Controls how many [random] ticks it takes for water to evaporate. The bigger this")
        .comment("number is (max 15), more time will take for water to evaporate, or set it to a")
        .comment("small value (min 1), to make this process faster.")
        .defineInRange("waterEvaporationTime", 8, 1, 15);

    private static final ForgeConfigSpec.BooleanValue m_lavaCooling = Builder
        .comment("------------------------------------------------------------------------------------------------------")
        .comment(" If true, lava placed by the player will eventually cooldown into a block defined by @lavaCoolingBlock")
        .define("lavaCooling", true);

    private static final ForgeConfigSpec.ConfigValue<String> m_lavaCoolingBlock = Builder
        .comment(" The block that lava will turn into after cooling down.")
        .define("lavaCoolingBlock", "minecraft:basalt");

    private static final ForgeConfigSpec.BooleanValue m_rainFlooding = Builder
        .comment("---------------------------------------------------------------------------------")
        .comment(" If true, puddles of water will appear during rain at random locations. The water")
        .comment(" will naturally flow down and it may even flood some areas. This behavior, together")
        .comment(" with @waterEvaporation will make the world feel more alive after a rainstorm.")
        .define("rainFlooding", true);

    private static final ForgeConfigSpec.IntValue m_rainFloodingInterval = Builder
        .comment("------------------------------------------------------------------------------------------")
        .comment(" The interval (in ticks) for spawning water during rain. Decreasing this value (down to 0)")
        .comment(" will make water appear more frequently during rain, but it may cause more lag.")
        .defineInRange("rainFloodingInterval", 12, 0, 100);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> m_enabledDimensions = Builder
        .comment("--------------------------------------------------------------")
        .comment(" Realistic water behavior will be enabled for these dimensions")
        .defineList("enabledDimensions", Defaults.enabledDimensions, ConfigUtils::isResourceLocation);

    public static final ForgeConfigSpec Spec = Builder.build();

    public static boolean waterEvaporation() {
        return m_waterEvaporation.get();
    }

    public static int getWaterEvapTime() {
        return m_waterEvaporationTime.get();
    }

    public static boolean lavaCooling() {
        return m_lavaCooling.get();
    }

    private static Block s_LavaCoolingBlock = Blocks.BASALT;

    public static BlockState lavaCoolingBlock() {
        return s_LavaCoolingBlock.defaultBlockState();
    }

    public static boolean rainFlooding() {
        return m_rainFlooding.get();
    }

    public static int rainFloodingInterval() {
        return m_rainFloodingInterval.get();
    }

    public static boolean enabledInDimension(ResourceKey<Level> dimension) {
        return m_enabledDimensions.get().contains(dimension.location().toString());
    }

    public static boolean isEnabled(Level level) {
        return enabledInDimension(level.dimension());
    }

    public static void reload() {
        final var key = ResourceLocation.parse(m_lavaCoolingBlock.get());
        final var block = ForgeRegistries.BLOCKS.getValue(key);
        s_LavaCoolingBlock = Objects.requireNonNullElse(block, Blocks.BASALT);
    }

    public static final class Defaults
    {
        public static final ImmutableList<String> enabledDimensions = ImmutableList.of(
            "minecraft:overworld",
            "minecraft:the_end"
        );
    }
}
