package github.jodevnull.waterflow.utils;

public final class PackedValue
{
    /**
     * Packs/unpacks a short and a byte in a single int
     */
    public static final class ShortByte
    {
        public static int pack(short shortVal, byte byteVal) {
            return ((shortVal & 0xFFFF) << 8) | (byteVal & 0xFF);
        }

        public static byte getByte(int packed) {
            return (byte) (packed & 0xFF);
        }

        public static short getShort(int packed) {
            return (short) ((packed >> 8) & 0xFFFF);
        }
    }
}
