package org.vstu.meaningtree.nodes.comparison;

import org.vstu.meaningtree.nodes.Expression;

import java.util.ArrayList;
import java.util.List;

public class CompoundComparison extends Expression {
    private final List<BinaryComparison> _comparisons;

    public CompoundComparison(BinaryComparison ... comparisons) {
        this._comparisons = List.of(comparisons);
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));

        for (BinaryComparison comparison : _comparisons) {
            builder.append(comparison.generateDot());
            builder.append(String.format("%s -- %s;\n", _id, comparison.getId()));
        }

        return builder.toString();
    }

    public List<BinaryComparison> get_comparisons() {
        return new ArrayList<>(_comparisons);
    }
}
