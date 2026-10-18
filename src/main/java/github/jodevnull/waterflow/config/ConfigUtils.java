package github.jodevnull.waterflow.config;

import github.jodevnull.waterflow.WaterFlow;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ConfigUtils
{
    public static ITag<Block> getBlockTag(String tagName) {
        final var tags = ForgeRegistries.BLOCKS.tags();

        if (tags == null) {
            WaterFlow.logger.error("Tags not loaded yet!");
            return null;
        }

        final TagKey<Block> tagKey = tags.createTagKey(ResourceLocation.parse(tagName.substring(1)));
        return tags.getTag(tagKey);
    }

    public static Set<Block> readList(List<? extends String> list, Predicate<Block> filter, boolean logInfo) {
        final Set<Block> result = new HashSet<>();

        for (final String str : list) {
            if (str.startsWith("#")) {
                result.addAll(readBlockTag(str, filter, logInfo));
            } else {
                final var key = ResourceLocation.parse(str);
                final Block block = ForgeRegistries.BLOCKS.getValue(key);

                if (block == null) {
                    WaterFlow.logger.error("Invalid block: {}", str);
                }

                else if (!filter.test(block)) {
                    result.add(block);
                }
            }
        }

        return result;
    }

    public static Set<Block> readBlockTag(String tagName, Predicate<Block> filter, boolean logInfo) {
        final Set<Block> result = new HashSet<>();
        final var tag = getBlockTag(tagName);

        if (tag == null) {
            return result;
        }

        for (final Block block : tag) {
            if (!filter.test(block)) {
                result.add(block);
            }
        }

        if (logInfo) WaterFlow.logger.info("Loaded {} blocks from tag {}", result.size(), tagName);

        return result;
    }

    public static void readBlockVolume(Object2ByteMap<Block> output, String str) {
        final var pair = splitVolume(str);
        final int amount = pair.right();
        final var location = pair.left();

        if (location.isEmpty()) {
            WaterFlow.logger.error("Could not read blockVolume format: {}", str);
            return;
        }

        if (amount < 0 || amount > 8) {
            WaterFlow.logger.error("Block volume range is (0, 8], got {}. At: {} (see config)", amount, str);
            return;
        }

        if (location.startsWith("#")) {
            final var tag = getBlockTag(location);

            if (tag != null) for (final var block : tag) {
                output.put(block, (byte) amount);
            }
        } else {
            final var key = ResourceLocation.parse(str);
            final var block = ForgeRegistries.BLOCKS.getValue(key);
            output.put(block, (byte) amount);
        }
    }

    public static boolean isResourceLocation(Object o) {
        if (o instanceof String str) {
            return ResourceLocation.isValidResourceLocation(str);
        }

        return false;
    }

    public static boolean isTagOrLocation(Object o) {
        if (o instanceof String str) {
            return isResourceLocation(str.startsWith("#") ? str.substring(1) : str);
        }

        return false;
    }

    private static final Pattern VOLUME = Pattern.compile("(?<id>.+?)\\s*=\\s*(?<v>[0-7])");

    public static boolean blockVolumeFormat(Object o) {
        if (o instanceof String str) {
            final var matcher = VOLUME.matcher(str);

            if (matcher.matches()) {
                return isTagOrLocation(matcher.group("id"));
            }
        }

        return false;
    }

    public static Pair<String, Integer> splitVolume(String str) {
        final var matcher = VOLUME.matcher(str);

        // this should be true
        if (matcher.matches()) {
            final int amount = Integer.parseInt(matcher.group("v"));
            return Pair.of(matcher.group("id").trim(), amount);
        }

        return Pair.of("", -1);
    }
}
