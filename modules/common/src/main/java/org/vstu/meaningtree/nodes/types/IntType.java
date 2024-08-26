package org.vstu.meaningtree.nodes.types;

public class IntType extends NumericType {
    public final boolean isUnsigned;
    public final int size;

    /**
     * Создаёт знаковый целочисленный тип размерностью 32
     */
    public IntType() {
        this(32, false);
    }

    /**
     * Создаёт целочисленный тип размером 32
     * @param isUnsigned флаг, является ли тип беззнаковым.
     */
    public IntType(boolean isUnsigned) {
        this(32, isUnsigned);
    }

    /**
     * Создаёт знаковый целочисленный тип
     * @param size размер типа.
     */
    public IntType(int size) {
        this(size, false);
    }

    /**
     * Создаёт целочисленный тип
     * @param isUnsigned флаг, является ли тип беззнаковым.
     * @param size размер типа.
     */
    public IntType(int size, boolean isUnsigned) {
        this.isUnsigned = isUnsigned;
        this.size = size;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
