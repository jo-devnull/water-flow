package github.jodevnull.waterflow.extension.waterlogging;

import github.jodevnull.waterflow.extension.waterlogging.interfaces.WL$Level;
import github.jodevnull.waterflow.foundation.config.Config;
import github.jodevnull.waterflow.foundation.core.fluid.FluidProperty;
import github.jodevnull.waterflow.foundation.core.fluid.FluidUtils;
import github.jodevnull.waterflow.foundation.utils.PackedValue;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class Waterlogging
{
    private static final Byte2ObjectMap<FluidState> WATER_STATES = new Byte2ObjectOpenHashMap<>();

    public static final int UPDATE_SCHEDULE_FLUID_TICK = 0x80;

    // IMPORTANT: for nimble pair, *first* is LEVEL, *second* is TIME
    public static void init() {
        WATER_STATES.defaultReturnValue(Fluids.EMPTY.defaultFluidState());

        for (byte level = 1; level <= 8; level++) {
            for (int time : FluidProperty.TIME.getPossibleValues()) {
                assert time <= 15;
                byte key = PackedValue.NimblePair.pack(level, (byte) time);
                WATER_STATES.put(key, FluidUtils.getWater(level).setValue(FluidProperty.TIME, time));
            }
        }
    }

    public static FluidState getWaterState(byte packed) {
        return WATER_STATES.get(packed);
    }

    public static byte pack(FluidState state) {
        assert FluidUtils.isWater(state);
        byte level = (byte) state.getAmount();
        byte time  = (byte) FluidProperty.timeof(state);
        return PackedValue.NimblePair.pack(level, time);
    }

    public static void handleWaterUpdate(BlockPos pos, FluidState water) {
        final ClientLevel level = Minecraft.getInstance().level;

        if (level != null) {
            ((WL$Level) level).wf_setWater(pos, water,
                Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
            );
        }
    }

    public static void handleSectionWaterUpdate(SectionPos sectionPos, short[] positions, FluidState[] states) {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null)
            return;

        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < positions.length; i++) {
            short pos = positions[i];
            mutPos.set(sectionPos.relativeToBlockX(pos), sectionPos.relativeToBlockY(pos), sectionPos.relativeToBlockZ(pos));

            ((WL$Level) level).wf_setWater(
                mutPos, states[i],
                Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
            );
        }
    }

    public static BlockState handleBlockRemoval(Level instance, BlockPos blockPos, int flags, int maxUpdateDepth) {
        final FluidState fluidState = FluidUtils.getFluidState(instance, blockPos);

        ((WL$Level) instance).wf_setWater(blockPos, Fluids.EMPTY.defaultFluidState(), flags, maxUpdateDepth);

        final int replacementFluidLevel = Mth.clamp(8 - fluidState.getAmount(), 0, 8);

        return fluidState.createLegacyBlock()
            .trySetValue(FluidProperty.TIME, Config.getEvaporationTime())
            .trySetValue(LiquidBlock.LEVEL, replacementFluidLevel);
    }

    public static boolean hasDifferentLightEmission(BlockGetter world, BlockPos pos, FluidState prevFluidState, FluidState newFluidState) {
        return prevFluidState.createLegacyBlock().getLightEmission(world, pos)
             != newFluidState.createLegacyBlock().getLightEmission(world, pos);
    }
}
