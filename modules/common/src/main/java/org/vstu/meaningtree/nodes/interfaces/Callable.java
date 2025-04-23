package org.vstu.meaningtree.nodes.interfaces;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public interface Callable {
    /**
     * @return возвращает выражение, обозначающее имя вызываемой сущности (например, обращение к методу как полю объекта)
     */
    Expression getCallableName();
    List<Expression> getArguments();
}
