package github.jodevnull.waterflow.core;

import github.jodevnull.waterflow.block.IWaterChannel;
import github.jodevnull.waterflow.core.fluid.FluidProperty;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import github.jodevnull.waterflow.registry.ModTags;
import github.jodevnull.waterflow.utils.WaterEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public enum WaterAction
{
    FLOW_CHANNEL       (WaterAction::flowChannel),
    FALL_IN_LAVA       (WaterAction::fallInLava),
    TURN_INTO_MOISTURE (WaterAction::turnIntoMoisture),
    FALL_INTO_SOURCE   (WaterAction::fallIntoSource),
    RESPECT_GRAVITY    (WaterAction::respectGravity); // This must be last

    private final Predicate<Context> action;

    WaterAction(Predicate<Context> action) {
        this.action = action;
    }

    public boolean dispatch(Context context) {
        return this.action.test(context);
    }

    // Newton would be proud
    private static boolean respectGravity(Context ctx) {
        final WaterSystem system = ctx.system();

        if (system.isNotFull(ctx.pos().below()) && system.canFlow(ctx.pos(), Direction.DOWN)) {
            system.setWater(ctx.pos(), 0);
            system.addWater(ctx.pos().below(), ctx.level(), Direction.DOWN);
            return true;
        } else {
            return false;
        }
    }

    private static boolean flowChannel(Context ctx) {
        if (IWaterChannel.isChannel(ctx.below())) {
            IWaterChannel.flowWaterAbove(ctx.system(), ctx.below(), ctx.pos(), ctx.level());
            return true;
        } else {
            return false;
        }
    }

    private static boolean fallInLava(Context ctx) {
        if (!ctx.below().is(Blocks.LAVA))
            return false;

        ctx.system().setWater(ctx.pos(), 0);
        final BlockPos belowPos = ctx.pos().below();

        if (ctx.level() == 8) {
            ctx.world().setBlockAndUpdate(belowPos, Blocks.OBSIDIAN.defaultBlockState());
        } else {
            final int time = FluidProperty.timeof(ctx.below());

            if (time < FluidProperty.MAX_TIME) {
                ctx.sections().setBlockState(belowPos, ctx.below().trySetValue(FluidProperty.TIME, time+1));
            } else {
                ctx.world().setBlockAndUpdate(belowPos, Blocks.OBSIDIAN.defaultBlockState());
            }
        }

        WaterEffects.fizz(ctx.world(), ctx.pos());
        return true;
    }

    private static boolean turnIntoMoisture(Context ctx) {
        if (ctx.below().getOptionalValue(FarmBlock.MOISTURE).orElse(8) == 8)
            return false;

        // If the block has MOISTURE, part of the water gets absorbed by it
        ctx.system().setWater(ctx.pos(), ctx.level() - 1);
        ctx.world().setBlock(ctx.pos().below(), ctx.below().trySetValue(FarmBlock.MOISTURE, 7), 2);
        return true;
    }

    private static boolean fallIntoSource(Context ctx) {
        if (!FluidUtils.isNaturalWater(ctx.sections(), ctx.pos().below()))
            return false;

        if (!ctx.below().is(ModTags.Blocks.NEEDS_WATER_SOURCE)) {
            ctx.system().setWater(ctx.pos(), 0);
        }

        return true;
    }

    public static final class Context
    {
        private WaterSystem system;
        private BlockPos    pos;
        private BlockState  below;
        private int         level;

        public WaterSystem system() {
            return system;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public BlockState below() {
            return below;
        }

        public int level() {
            return level;
        }

        public ServerLevel world() {
            return this.system.getWorld();
        }

        public SectionCache sections() {
            return this.system.getSections();
        }

        public void setSystem(WaterSystem system) {
            this.system = system;
        }

        public void setBelow(BlockState below) {
            this.below = below;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setPos(BlockPos pos) {
            this.pos = pos;
        }
    }
}
