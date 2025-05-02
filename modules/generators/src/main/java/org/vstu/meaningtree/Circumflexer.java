package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Node;

import java.util.List;

public class Circumflexer {
    private AugletProblem _problem;
    private List<Node> _uniqueNodes;

    public void setProblem(AugletProblem problem) {
        _problem = problem;
    }

    public void problemMode() {
        _uniqueNodes = _problem.meta().uniqueProblemNodes();
    }

    public void solutionMode() {
        _uniqueNodes = _problem.meta().uniqueSolutionNodes();
    }

    public String circumflexifyUnique(String code, Node node) {
        if (_uniqueNodes.contains(node)) {
            return circumflexify(code);
        }
        return code;
    }

    public String circumflexify(String code) {
        return "^^" + code + "^";
    }

}
