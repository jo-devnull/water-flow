package github.jodevnull.waterflow;

import github.jodevnull.waterflow.utils.Utils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags
{
    public static final class Blocks
    {
        public static final TagKey<Block> WATER_CHANNELS = Utils.tagBlock("water_channels");
        public static final TagKey<Block> NEEDS_WATER_SOURCE = Utils.tagBlock("needs_water_source");
        public static final TagKey<Block> EARTHY_CHANNELS = Utils.tagBlock("earthy_channels");
    }

    public static final class Items
    {
        public static final TagKey<Item> CAN_PUSH_WATER = Utils.tagItem("can_push_water");
    }
}
