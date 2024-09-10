package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;

public class CharacterType extends Type {
    public final int size;

    /**
     * Создаёт тип символ размером 8 бит
     */
    public CharacterType() {
        size = 8;
    }

    /**
     * Создаёт тип символ
     * @param bits количество бит
     * */
    public CharacterType(int bits) {
        size = bits;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
