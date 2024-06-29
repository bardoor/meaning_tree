package org.vstu.meaningtree;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.function.Predicate;

public class Combinator<T> {
    protected final List<T> _elements;

    public Combinator(List<T> elements) {
        _elements = elements;
    }

    @SafeVarargs
    public Combinator(T ... elements) {
        _elements = List.of(elements);
    }

    public List<ImmutablePair<T, T>> getPermutations() {
        return _elements.stream()
                    .flatMap(element -> _elements.stream()
                            .filter(Predicate.not(element::equals))
                            .map(otherElement -> new ImmutablePair<>(element, otherElement)))
                    .toList();
    }

}
