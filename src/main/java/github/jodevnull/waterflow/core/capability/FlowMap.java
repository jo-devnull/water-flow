package github.jodevnull.waterflow.core.capability;

import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class FlowMap
{
    private final Short2ByteMap flowMap = new Short2ByteOpenHashMap();

    public FlowMap() {
        flowMap.defaultReturnValue((byte) 0);
    }

    public Short2ByteMap getFlowMap() {
        return flowMap;
    }

    public Direction getFlow(BlockPos pos) {
        return Direction.from3DDataValue(flowMap.get(packed(pos)));
    }

    public Direction setFlow(BlockPos pos, Direction flow) {
        if (flow == Direction.DOWN) {
            return Direction.from3DDataValue(flowMap.remove(packed(pos)));
        }

        return Direction.from3DDataValue(
            flowMap.put(packed(pos), (byte) flow.get3DDataValue())
        );
    }

    private short packed(BlockPos pos) {
        int i = pos.getX() & 15;
        int j = pos.getY() & 15;
        int k = pos.getZ() & 15;
        return (short) (i << 8 | j << 4 | k);
    }
}
