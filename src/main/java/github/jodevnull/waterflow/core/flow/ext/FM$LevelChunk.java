package github.jodevnull.waterflow.core.flow.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface FM$LevelChunk
{
    Direction wf_getFlow(BlockPos pos);
    Direction wf_setFlow(BlockPos pos, Direction flow);
}
