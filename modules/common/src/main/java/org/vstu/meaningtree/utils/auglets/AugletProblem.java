package org.vstu.meaningtree.utils.auglets;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.MeaningTree;

public record AugletProblem(MeaningTree meaningTree, @Nullable AugletsMeta meta) {
}
