package org.vstu.meaningtree.nodes.enums;

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

    public String getValue(){
        return _op;
    }
}
