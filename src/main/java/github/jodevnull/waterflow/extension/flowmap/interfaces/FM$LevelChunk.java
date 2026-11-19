package github.jodevnull.waterflow.extension.flowmap.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface FM$LevelChunk
{
    Direction wf_getFlow(BlockPos pos);
    Direction wf_setFlow(BlockPos pos, Direction flow);
}
