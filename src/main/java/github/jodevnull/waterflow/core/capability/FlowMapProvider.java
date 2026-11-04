package github.jodevnull.waterflow.core.capability;

import github.jodevnull.waterflow.utils.PackedValue;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntArrayTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FlowMapProvider implements ICapabilitySerializable<IntArrayTag>
{
    private final Short2ByteMap flowMap = new Short2ByteOpenHashMap();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return null;
    }

    @Override
    public IntArrayTag serializeNBT() {
        final int[] data = new int[flowMap.size()];

        int i = 0;
        for (final var entry : flowMap.short2ByteEntrySet()) {
            short key = entry.getShortKey();
            byte  val = entry.getByteValue();
            data[i++] = PackedValue.ShortByte.pack(key, val);
        }

        return new IntArrayTag(data);
    }

    @Override
    public void deserializeNBT(IntArrayTag nbt) {
        flowMap.clear();

        for (final int packed : nbt.getAsIntArray()) {
            short key = PackedValue.ShortByte.getShort(packed);
            byte  val = PackedValue.ShortByte.getByte(packed);
            flowMap.put(key, val);
        }

        assert nbt.size() == flowMap.size();
    }
}
