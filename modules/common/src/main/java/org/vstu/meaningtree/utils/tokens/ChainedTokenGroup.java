package org.vstu.meaningtree.utils.tokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ChainedTokenGroup extends TokenGroup {
    private final List<TokenGroup> groups;

    public ChainedTokenGroup(TokenList source, TokenGroup ... groups) {
        super(Arrays.stream(groups).min(
                Comparator.comparingInt(o -> o.start)
        ).map((tokenGroup) -> tokenGroup.start).orElse(0), Arrays.stream(groups).max(
                Comparator.comparingInt(o -> o.stop)
        ).map((tokenGroup) -> tokenGroup.stop).orElse(0), source);

        this.groups = Stream.of(groups).sorted(Comparator.comparingInt(t -> t.start)).toList();
    }

    public List<TokenGroup> chainedGroups() {
        return new ArrayList<>(groups);
    }
}
