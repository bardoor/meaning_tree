package org.vstu.meaningtree.nodes.comparison;

import org.vstu.meaningtree.nodes.Expression;

import java.util.ArrayList;
import java.util.List;

public class CompoundComparison extends Expression {
    private final List<BinaryComparison> comparisons;

    public CompoundComparison(List<BinaryComparison> comparisons) {
        this.comparisons = comparisons;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public List<BinaryComparison> getComparisons() {
        return new ArrayList<>(comparisons);
    }
}
