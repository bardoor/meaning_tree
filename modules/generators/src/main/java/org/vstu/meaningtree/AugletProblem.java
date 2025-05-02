package org.vstu.meaningtree;

import org.jetbrains.annotations.Nullable;

public record AugletProblem(
        MeaningTree problemMeaningTree,
        MeaningTree solutionMeaningTree,
        AugletsMeta meta,
        AugletsRefactorProblemsType problemType) {
    public AugletProblem(MeaningTree problem, MeaningTree solution, AugletsRefactorProblemsType problemType) {
        this(problem, solution, new AugletsMeta(), problemType);
    }
}
