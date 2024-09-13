package org.vstu.meaningtree.nodes.types.builtin;

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
}
