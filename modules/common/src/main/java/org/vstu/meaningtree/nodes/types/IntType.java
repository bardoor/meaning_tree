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
     * @param isUnsigned флаг, является ли тип беззнаковым.
     * Создаёт целочисленный тип размером 32
     */
    public IntType(boolean isUnsigned) {
        this(32, isUnsigned);
    }

    /**
     * @param size размер типа.
     * Создаёт знаковый целочисленный тип
     */
    public IntType(int size) {
        this(size, false);
    }

    /**
     * @param size размер типа.
     * @param isUnsigned флаг, является ли тип беззнаковым.
     * Создаёт целочисленный тип
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
