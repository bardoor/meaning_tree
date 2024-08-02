package org.vstu.meaningtree;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class TestCombinator {
    public static List<ImmutablePair<SingleTestCode, TestCodeGroup>> getPairs(TestCase case_) {
        if (!case_.hasMainCode()) {
            Combinator<TestCodeGroup> comb = new Combinator<TestCodeGroup>(case_.getCodeGroups());
            List<ImmutablePair<TestCodeGroup, TestCodeGroup>> pairs = comb.getPermutations();
            ArrayList<ImmutablePair<SingleTestCode, TestCodeGroup>> result = new ArrayList<>();
            for (ImmutablePair<TestCodeGroup, TestCodeGroup> pair : pairs) {
                if (pair.getLeft().size() == 1) {
                    result.add(new ImmutablePair<>(pair.getLeft().getFirst(), pair.getRight()));
                }
            }
            return result;
        }

        ArrayList<ImmutablePair<SingleTestCode, TestCodeGroup>> result = new ArrayList<>();
        for (TestCodeGroup code : case_.getCodeGroups()) {
            result.add(new ImmutablePair<>(case_.getMainCode(), code));
        }
        return result;
    }
}
