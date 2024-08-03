package org.vstu.meaningtree;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class TestCombinator {
    public static List<ImmutablePair<TestCodeGroup, TestCodeGroup>> getPairs(TestCase case_) {
        if (!case_.hasMainCode()) {
            Combinator<TestCodeGroup> comb = new Combinator<TestCodeGroup>(case_.getCodeGroups());
            return comb.getPermutations();
        }

        ArrayList<ImmutablePair<TestCodeGroup, TestCodeGroup>> result = new ArrayList<>();
        for (TestCodeGroup code : case_.getCodeGroups()) {
            result.add(new ImmutablePair<>(new TestCodeGroup(case_.getMainCode().getLanguage(), case_.getMainCode()), code));
        }
        return result;
    }
}
