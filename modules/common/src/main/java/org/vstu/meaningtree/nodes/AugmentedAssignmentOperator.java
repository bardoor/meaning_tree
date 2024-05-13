package org.vstu.meaningtree.nodes;

public enum AugmentedAssignmentOperator {
    NONE("="),
    ADD("+="),
    SUB("-="),
    MUL("*="),
    DIV("/="),
    FLOOR_DIV("//="),
    BITWISE_AND("&="),
    BITWISE_OR("|="),
    BITWISE_XOR("^="),
    BITWISE_SHIFT_LEFT("<<="),
    BITWISE_SHIFT_RIGHT(">>="),
    MOD("%="),
    POW("**=");

    private final String _op;
    AugmentedAssignmentOperator(String op) {
        _op = op;
    }

    @Override
    public String toString(){
        return _op;
    }
}
