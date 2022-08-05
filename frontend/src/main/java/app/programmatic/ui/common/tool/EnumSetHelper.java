package app.programmatic.ui.common.tool;

import java.util.BitSet;
import java.util.EnumSet;

public class EnumSetHelper {

    public static final int MAX_ENUM_SIZE = Long.SIZE - 1;

    public static <T extends Enum<T>> EnumSet<T> bitsToEnumSet(Class<T> enumType, long bits) {
        EnumSet<T> result = EnumSet.noneOf(enumType);
        if (bits == 0) {
            return result;
        }

        T[] values = enumType.getEnumConstants();
        if (Long.bitCount(bits) > values.length) {
            throw new IndexOutOfBoundsException(String.format("Bits in %d out of bounds of %s", bits, enumType.getName()));
        }

        BitSet bitSet = BitSet.valueOf(new long[] { bits } );
        for (T enumElement : values) {
            if (bitSet.get(enumElement.ordinal())) {
                result.add(enumElement);
            }
        }

        return result;
    }

    public static <T extends Enum<T>> long enumSetToBits(Class<T> enumType, EnumSet<T> valueSet) {
        if (valueSet == null || valueSet.isEmpty()) {
            return 0;
        }

        BitSet result = new BitSet(MAX_ENUM_SIZE);

        if (valueSet.size() > MAX_ENUM_SIZE) {
            throw new IndexOutOfBoundsException(String.format("Values of %s out of bounds of %d", enumType.getName(), MAX_ENUM_SIZE));
        }

        for (T enumElement : enumType.getEnumConstants()) {
            if (valueSet.contains(enumElement)) {
                result.set(enumElement.ordinal());
            }
        }

        return result.toLongArray()[0];
    }
}
