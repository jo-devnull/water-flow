package github.jodevnull.waterflow.foundation.utils;

public final class PackedValue
{
    public static final class NimblePair
    {
        public static byte pack(byte first, byte second) {
            return (byte) (((second & 0x0F) << 4) | (first & 0x0F));
        }

        public static byte second(byte packed) {
            return (byte) ((packed >> 4) & 0x0F);
        }

        public static byte first(byte packed) {
            return (byte) (packed & 0x0F);
        }
    }

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
