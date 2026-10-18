package github.jodevnull.waterflow.config;

import com.google.common.collect.ImmutableList;
import github.jodevnull.waterflow.WaterFlow;
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

    private static final ForgeConfigSpec.BooleanValue m_realisticFluids = Builder
        .comment(" Enable realistic fluid flow.")
        .comment("")
        .comment(" Only fluids the player interacted with (placing water with a bucket or through create machines) will")
        .comment(" be affected. Naturally generated fluids in the world (like water in rivers and oceans) will not be")
        .comment(" affected, unless @ignoreNaturalBlocks is set to false.")
        .define("realisticFluids", true);

    private static final ForgeConfigSpec.BooleanValue m_waterEvaporation = Builder
        .comment("-------------------------------------------------------------------------------------------")
        .comment(" If true, small pockets of water will slowly evaporate over time. This does not affect full")
        .comment(" blocks of water.")
        .define("waterEvaporation", true);

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
        .defineInRange("rainFloodingInterval", 15, 0, 100);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> m_enabledDimensions = Builder
        .comment("--------------------------------------------------------------")
        .comment(" Realistic water behavior will be enabled for these dimensions")
        .defineList("enabledDimensions", Defaults.enabledDimensions, ConfigUtils::isResourceLocation);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> m_waterFlowsThrough = Builder
        .comment("---------------------------------------------------------------------------")
        .comment(" A list of block (or block tags) that water should be able to flow through.")
        .comment(" For example, non full solid blocks like grass or fences.")
        .comment("")
        .comment(" Format: namespace:block or #tag:block")
        .defineList("waterFlowsThrough", Defaults.waterFlowsThrough, ConfigUtils::isTagOrLocation);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> m_waterFlowBlackList = Builder
        .comment("----------------------------------------------------------------------------------------")
        .comment(" Black list of @waterFlowsThrough. Water will not flow through blocks added to this list")
        .comment("")
        .comment(" Format: namespace:block or #tag:block")
        .defineList("waterFlowBlackList", Defaults.waterFlowBlacklist, ConfigUtils::isTagOrLocation);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> m_BlockVolume = Builder
        .comment("----------------------------------------------------------------------------------")
        .comment(" Define the amount that a block should displace water (if the block has a volume).")
        .comment("")
        .comment(" Format: namespace:block=volume or #tag:block=volume")
        .defineList("blockVolume", Defaults.blockVolume, ConfigUtils::blockVolumeFormat);

    public static final ForgeConfigSpec Spec = Builder.build();

    public static boolean realisticFluids() {
        return m_realisticFluids.get();
    }

    public static boolean waterEvaporation() {
        return m_waterEvaporation.get();
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

    private static Set<Block> s_WaterFlowsThrough = new HashSet<>();

    public static boolean isFluidloggable(BlockState state) {
        return s_WaterFlowsThrough.contains(state.getBlock());
    }

    private static final Object2ByteMap<Block> s_BlockVolume = new Object2ByteOpenHashMap<>();

    public static int getVolume(BlockState state) {
        return s_BlockVolume.getByte(state.getBlock());
    }

    public static void reload() {
        final Set<Block> blackList = ConfigUtils.readList(m_waterFlowBlackList.get(), block -> false, false);
        s_WaterFlowsThrough = ConfigUtils.readList(m_waterFlowsThrough.get(), blackList::contains, true);

        { // load lavaCoolingBlock
            final var key = ResourceLocation.parse(m_lavaCoolingBlock.get());
            final var block = ForgeRegistries.BLOCKS.getValue(key);
            s_LavaCoolingBlock = Objects.requireNonNullElse(block, Blocks.BASALT);
        }


        // load block volumes
        s_BlockVolume.clear();
        s_BlockVolume.defaultReturnValue((byte) 0);
        for (final var str : m_BlockVolume.get()) {
            ConfigUtils.readBlockVolume(s_BlockVolume, str);
        }

        WaterFlow.logger.info("Defined volume for {} block(s)", s_BlockVolume.size());
    }

    public static final class Defaults
    {
        public static final ImmutableList<String> enabledDimensions = ImmutableList.of(
            "minecraft:overworld",
            "minecraft:the_end"
        );

        public static final ImmutableList<String> waterFlowsThrough = ImmutableList.of(
            "#mercury:water_flows_through"
        );

        public static final ImmutableList<String> waterFlowBlacklist = ImmutableList.of(
            "#mercury:water_flows_through_blacklist"
        );

        public static final ImmutableList<String> blockVolume = ImmutableList.of(
            "#minecraft:trapdoors=3"
        );
    }
}
