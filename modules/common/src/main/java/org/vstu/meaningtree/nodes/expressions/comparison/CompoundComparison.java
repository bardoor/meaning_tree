package org.vstu.meaningtree.nodes.expressions.comparison;

import org.vstu.meaningtree.nodes.Expression;

import java.util.ArrayList;
import java.util.List;

public class CompoundComparison extends Expression {
    private final List<BinaryComparison> _comparisons;

    /**
     * @param comparisons - отсортированный список сравнений (example of view: {a < b, b < c} => a < b < c)
     */
    public CompoundComparison(BinaryComparison ... comparisons) {
        for (int i = 0; i < comparisons.length - 1; i++) {
            if (!comparisons[i].getRight().equals(comparisons[i+1].getLeft())) {
                throw new IllegalArgumentException("Comparisons must be sorted. In pair of comparisons right operand of first must be equal to left operand of second comparison");
            }
        }
        this._comparisons = List.of(comparisons);
    }

    public CompoundComparison(List<BinaryComparison> members) {
        this(members.toArray(new BinaryComparison[0]));
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

    public List<BinaryComparison> getComparisons() {
        return new ArrayList<>(_comparisons);
    }
}
