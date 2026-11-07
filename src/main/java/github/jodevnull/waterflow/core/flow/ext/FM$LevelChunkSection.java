package github.jodevnull.waterflow.core.flow.ext;

import it.unimi.dsi.fastutil.shorts.Short2ByteMap;

public interface FM$LevelChunkSection
{
    Short2ByteMap wf_createFlowMap();
    Short2ByteMap wf_getFlowMap();

    byte wf_getFlow(int x, int y, int z);
    byte wf_setFlow(int x, int y, int z, int flow);
}
