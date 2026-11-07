package github.jodevnull.waterflow.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

public final class Config
{
    private static final ForgeConfigSpec.Builder Builder = new ForgeConfigSpec.Builder();
    public  static final ForgeConfigSpec Spec;

    // [general]
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> mEnabledDimensions;

    // [water]
    private static final ForgeConfigSpec.BooleanValue mWaterEvaporation;
    private static final ForgeConfigSpec.IntValue mWaterEvaporationTime;

    // [lava]
    private static final ForgeConfigSpec.BooleanValue mLavaCooldown;
    private static final ForgeConfigSpec.ConfigValue<String> mLavaCooldownBlock;

    // [rain]
    private static final ForgeConfigSpec.BooleanValue mRainWater;
    private static final ForgeConfigSpec.IntValue mRainWaterInterval;
    private static final ForgeConfigSpec.IntValue mRainWaterIntensity;
    private static final ForgeConfigSpec.IntValue mHeavyRainWaterInterval;
    private static final ForgeConfigSpec.IntValue mHeavyRainWaterIntensity;

    static {
        mEnabledDimensions = Builder
            .comment("The mod will be enabled for these dimensions")
            .defineList("enabled_dimensions", Defaults.enabledDimensions, ConfigUtils::isResourceLocation);

        mWaterEvaporation = Builder
            .comment("If true, small pockets of water will slowly evaporate over time.")
            .comment("This does not affect full blocks of water.")
            .define("water_evaporation", true);

        mWaterEvaporationTime = Builder
            .comment("The amount of random ticks it takes for water to evaporate. Set it to")
            .comment("a low value to make water evaporate faster.")
            .defineInRange("water_evaporation_time", 8, 1, 15);

        mLavaCooldown = Builder
            .comment("If true, lava placed by the player will eventually cooldown into a block.")
            .define("lava_cools_down", true);

        mLavaCooldownBlock = Builder
            .comment(" The block that lava will turn into after cooling down.")
            .define("lava_cools_into", "minecraft:basalt");

        Builder.push("rain");

        mRainWater = Builder
            .comment("If true, water will spawn during rain.")
            .define("rain_water", true);

        mRainWaterInterval = Builder
            .comment("The interval (in ticks) for spawning water during normal rain.")
            .defineInRange("rain_water_interval", 15, 0, 100);

        mRainWaterIntensity = Builder
            .comment("The amount of water to spawn during normal rain.")
            .defineInRange("rain_water_interval", 1, 1, 8);

        mHeavyRainWaterInterval = Builder
            .comment(" The interval (in ticks) to spawn water during heavy rain.")
            .defineInRange("rain_flooding_interval", 12, 0, 100);

        mHeavyRainWaterIntensity = Builder
            .comment("The amount of water to spawn during heavy rain.")
            .defineInRange("rain_water_interval", 2, 1, 8);

        Builder.pop();

        Spec = Builder.build();
    }

    public static boolean enabledInDimension(ResourceKey<Level> dimension) {
        return mEnabledDimensions.get().contains(dimension.location().toString());
    }

    public static boolean waterEvaporationEnabled() {
        return mWaterEvaporation.get();
    }

    public static int getEvaporationTime() {
        return mWaterEvaporationTime.get();
    }

    public static boolean lavaCooldownEnabled() {
        return mLavaCooldown.get();
    }

    private static Block mLavaCoolsIntoBlock = Blocks.BASALT;

    public static BlockState blockThatLavaCoolsInto() {
        return mLavaCoolsIntoBlock.defaultBlockState();
    }

    public static boolean rainWaterEnabled() {
        return mRainWater.get();
    }

    public static int rainWaterInterval() {
        return mRainWaterInterval.get();
    }

    public static int rainWaterIntensity() {
        return mRainWaterIntensity.get();
    }

    public static int rainFloodingInterval() {
        return mHeavyRainWaterInterval.get();
    }

    public static int rainFloodingIntensity() {
        return mHeavyRainWaterIntensity.get();
    }

    public static boolean isEnabled(Level level) {
        return enabledInDimension(level.dimension());
    }

    public static void reload() {
        final var key = ResourceLocation.parse(mLavaCooldownBlock.get());
        final var block = ForgeRegistries.BLOCKS.getValue(key);
        mLavaCoolsIntoBlock = Objects.requireNonNullElse(block, Blocks.BASALT);
    }

    public static final class Defaults
    {
        public static final ImmutableList<String> enabledDimensions = ImmutableList.of(
            "minecraft:overworld",
            "minecraft:the_end"
        );
    }
}
