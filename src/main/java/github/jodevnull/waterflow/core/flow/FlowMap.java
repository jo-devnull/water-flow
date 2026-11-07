package github.jodevnull.waterflow.core.flow;

import github.jodevnull.waterflow.core.SectionCache;
import github.jodevnull.waterflow.core.flow.ext.FM$LevelChunkSection;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LiquidBlock;

public final class FlowMap
{
    private final Long2ByteMap cachedFlow = new Long2ByteOpenHashMap();
    private final LongSet removeQueue = new LongOpenHashSet();
    private final SectionCache sections;

    public FlowMap(SectionCache sections) {
        this.sections = sections;
        this.cachedFlow.defaultReturnValue((byte) 0);
    }

    public Direction getFlow(BlockPos pos) {
        if (removeQueue.contains(pos.asLong())) {
            return Direction.DOWN;
        }

        return Direction.from3DDataValue(
            cachedFlow.computeIfAbsentNullable(pos.asLong(), lpos -> {
                final byte value = cacheFlow(lpos);
                return value == 0 ? null : value;
            })
        );
    }

    public byte cacheFlow(long packedPos) {
        final var section = sections.getSection(BlockPos.of(packedPos));

        if (section.hasOnlyAir())
            return (byte) 0;

        int rx = BlockPos.getX(packedPos) & 15;
        int ry = BlockPos.getY(packedPos) & 15;
        int rz = BlockPos.getZ(packedPos) & 15;

        return ((FM$LevelChunkSection) section).wf_getFlow(rx, ry, rz);
    }

    public Direction setFlow(BlockPos pos, Direction flow) {
        final var section = sections.getSection(pos);

        if (flow == null) {
            cachedFlow.remove(pos.asLong());
        }

        if (!this.sections.world.isLoaded(pos)) {
            cachedFlow.remove(pos.asLong());
            removeQueue.remove(pos.asLong());
            return Direction.DOWN;
        }

        if (section.hasOnlyAir())
            return Direction.DOWN;

        int rx = pos.getX() & 15;
        int ry = pos.getY() & 15;
        int rz = pos.getZ() & 15;

        byte flowValue = (byte) (flow == null ? 0 : flow.get3DDataValue());
        byte prev = ((FM$LevelChunkSection) section).wf_setFlow(rx, ry, rz, flowValue);

        if (prev != flowValue) {
            if (prev != 0) {
                this.cachedFlow.put(pos.asLong(), flowValue);
            }

            this.sections.world.getChunkAt(pos).setUnsaved(true);
        }

        return Direction.from3DDataValue(prev);
    }

    public void removeFlowAt(BlockPos pos) {
        this.removeQueue.add(pos.asLong());
        this.cachedFlow.remove(pos.asLong());
    }

    public void updateNeighbors(BlockPos pos) {
        removeQueue.add(pos.asLong());
        cachedFlow.remove(pos.asLong());

        for (final Direction dir : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            final long neighbor = pos.relative(dir).asLong();
            removeQueue.add(neighbor);
            cachedFlow.remove(neighbor);
        }
    }

    public void updateFlowAt(BlockPos pos) {
        final BlockPos A = pos.offset(-4,  0, -4);
        final BlockPos B = pos.offset( 4,  1,  4);

        for (final BlockPos neighbor : BlockPos.betweenClosed(A, B)) {
            removeQueue.add(neighbor.asLong());
            cachedFlow.remove(neighbor.asLong());
        }
    }

    private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();

    public void tick() {
        for (final long marked : removeQueue) {
            setFlow(scratchPos.set(marked), null);
        }
    }

    public void clear() {
        this.cachedFlow.clear();
        this.removeQueue.clear();
    }
}
