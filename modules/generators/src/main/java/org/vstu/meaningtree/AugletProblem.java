package org.vstu.meaningtree;

import org.jetbrains.annotations.Nullable;

public record AugletProblem(
        MeaningTree problemMeaningTree,
        MeaningTree solutionMeaningTree,
        AugletsMeta meta) {
    public AugletProblem(MeaningTree problem, MeaningTree solution) {
        this(problem, solution, new AugletsMeta());
    }
}
