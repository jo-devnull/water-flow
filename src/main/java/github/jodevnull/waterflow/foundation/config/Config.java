package github.jodevnull.waterflow.foundation.config;

import com.google.common.collect.ImmutableList;
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
    public  static final ForgeConfigSpec Spec;

    // [general]
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> mEnabledDimensions;

    // [water]
    private static final ForgeConfigSpec.BooleanValue mWaterEvaporation;
    private static final ForgeConfigSpec.IntValue mWaterEvaporationTime;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> mWaterBreakable;
    private static final Set<Block> sWaterBreakable = new HashSet<>();

    // [lava]
    private static final ForgeConfigSpec.BooleanValue mLavaCooldown;
    private static final ForgeConfigSpec.ConfigValue<String> mLavaCooldownBlock;

    // [rain]
    private static final ForgeConfigSpec.BooleanValue mRainWater;
    private static final ForgeConfigSpec.IntValue mRainWaterInterval;
    private static final ForgeConfigSpec.IntValue mRainWaterIntensity;
    private static final ForgeConfigSpec.IntValue mHeavyRainWaterInterval;
    private static final ForgeConfigSpec.IntValue mHeavyRainWaterIntensity;

    // [waterlogging]
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> mWaterLoggable;
    private static final Set<Block> sWaterLoggable = new HashSet<>();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> mAlwaysFlowThrough;
    private static final Set<Block> sAlwaysFlowThroughBlocks = new HashSet<>();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> mWaterLoggableBlackList;

    static {
        mEnabledDimensions = Builder
            .worldRestart()
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

        mWaterBreakable = Builder
            .comment("Defines which blocks water should break when in contact")
            .defineList("water_breakable", Defaults.waterBreakable, ConfigUtils::isTagOrLocation);

        mLavaCooldown = Builder
            .comment("If true, lava placed by the player will eventually cooldown into a block.")
            .define("lava_cools_down", true);

        mLavaCooldownBlock = Builder
            .comment(" The block that lava will turn into after cooling down.")
            .define("lava_cools_into", "minecraft:basalt");

        mRainWater = Builder
            .comment("If true, water will spawn during rain.")
            .define("rain_water", true);

        mRainWaterInterval = Builder
            .comment("The interval (in ticks) for spawning water during normal rain.")
            .defineInRange("rain_water_interval", 20, 0, 100);

        mRainWaterIntensity = Builder
            .comment("The amount of water to spawn during normal rain.")
            .defineInRange("rain_water_interval", 1, 1, 8);

        mHeavyRainWaterInterval = Builder
            .comment(" The interval (in ticks) to spawn water during heavy rain.")
            .defineInRange("rain_flooding_interval", 15, 0, 100);

        mHeavyRainWaterIntensity = Builder
            .comment("The amount of water to spawn during heavy rain.")
            .defineInRange("rain_water_interval", 1, 1, 8);

        Builder.push("waterlogging");

        mWaterLoggable = Builder
            .comment("Define which blocks water should be able to flow through")
            .defineList("waterloggable", Defaults.waterloggable, ConfigUtils::isTagOrLocation);

        mWaterLoggableBlackList = Builder
            .comment("Define which blocks water should NOT flow through it.")
            .defineList("waterloggable_blacklist", Defaults.empty, ConfigUtils::isTagOrLocation);

        mAlwaysFlowThrough = Builder
            .comment("Some blocks may block the flow of water depending on it's block state. This")
            .comment("option bypass this behaviour, allowing water to flow through it regardless")
            .defineList("always_flow_through", Defaults.alwaysFlowThrough, ConfigUtils::isTagOrLocation);

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

    public static boolean alwaysFlowsThrough(BlockState state) {
        return sAlwaysFlowThroughBlocks.contains(state.getBlock());
    }

    public static boolean isWaterBreakable(BlockState state) {
        return sWaterBreakable.contains(state.getBlock());
    }

    public static boolean isWaterloggable(BlockState state) {
        return sWaterLoggable.contains(state.getBlock());
    }

    public static void reload() {
        final var key = ResourceLocation.parse(mLavaCooldownBlock.get());
        final var block = ForgeRegistries.BLOCKS.getValue(key);
        mLavaCoolsIntoBlock = Objects.requireNonNullElse(block, Blocks.BASALT);

        sAlwaysFlowThroughBlocks.clear();
        sAlwaysFlowThroughBlocks.addAll(ConfigUtils.readList(mAlwaysFlowThrough.get(), b -> false, true));

        sWaterBreakable.clear();
        sWaterBreakable.addAll(ConfigUtils.readList(mWaterBreakable.get(), b -> false, true));

        /* waterloggables */ {
            final Set<Block> blacklist = ConfigUtils.readList(mWaterLoggableBlackList.get(), b -> false, true);

            sWaterLoggable.clear();
            sWaterLoggable.addAll(ConfigUtils.readList(mWaterLoggable.get(), blacklist::contains, true));
        }
    }

    public static final class Defaults
    {
        public static final ImmutableList<String> empty = ImmutableList.of();

        public static final ImmutableList<String> enabledDimensions = ImmutableList.of(
            "minecraft:overworld",
            "minecraft:the_end"
        );

        public static final ImmutableList<String> alwaysFlowThrough = ImmutableList.of(
            "minecraft:iron_trapdoor"
        );

        public static final ImmutableList<String> waterBreakable = ImmutableList.of(
            "minecraft:snow",
            "minecraft:snow_block"
        );

        public static final ImmutableList<String> waterloggable = ImmutableList.of();
    }
}
