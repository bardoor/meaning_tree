package org.vstu.meaningtree.nodes.types.builtin;

import java.util.Objects;

public class FloatType extends NumericType {
    public final int size;

    /**
     * Создаёт тип числа с плавающей точкой размером 32 бита
     */
    public FloatType() {
        size = 32;
    }

    /**
     * Создаёт тип числа с плавающей точкой
     * @param bits количество бит
     * */
    public FloatType(int bits) {
        size = bits;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FloatType floatType = (FloatType) o;
        return size == floatType.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), size);
    }
}
