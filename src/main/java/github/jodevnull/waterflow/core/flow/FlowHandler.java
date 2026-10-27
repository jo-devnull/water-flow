package github.jodevnull.waterflow.core.flow;

import github.jodevnull.waterflow.core.api.IWaterFlow;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class FlowHandler
{
    private static final byte[] MASK = new byte[6];

    static {
        for (byte i = 0; i < 6; i++) {
            MASK[i] = (byte) ((1 & 0x0f) << i);
        }
    }

    public static byte all() {
        return (byte) 0;
    }

    public static byte none() {
        return (byte) (
            MASK[0] |
            MASK[1] |
            MASK[2] |
            MASK[3] |
            MASK[4] |
            MASK[5]
        );
    }

    public static byte get(Direction dir) {
        return MASK[dir.ordinal()];
    }

    public static boolean checkFlowIn(short states, Direction dir) {
        return (states & get(dir)) == get(dir);
    }

    public static boolean checkFlowOut(short states, Direction dir) {
        final short mask = (short) (((short) get(dir)) << 8);
        return (states & mask) == mask;
    }

    public static short pack(byte flowIn, byte flowOut) {
        return (short) ((((short) flowOut) << 8) | ((short) flowIn));
    }

    public static short getFlowState(BlockState state) {
        if (state.isAir() || state.is(Blocks.WATER)) {
            return pack(all(), all());
        }

        final byte flowIn = ((IWaterFlow) state.getBlock()).blockFlowIn(state);
        final byte flowOut = ((IWaterFlow) state.getBlock()).blockFlowOut(state);
        return pack(flowIn, flowOut);
    }
}
