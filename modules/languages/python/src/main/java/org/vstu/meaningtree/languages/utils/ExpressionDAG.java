package org.vstu.meaningtree.languages.utils;

import org.vstu.meaningtree.nodes.Expression;

import java.util.*;

public class ExpressionDAG {
    private HashMap<Expression, ArrayList<Expression>> edges;
    private HashMap<Expression, Expression> specialTaggedEdges;

    public ExpressionDAG(Expression ... vertices) {
        edges = new HashMap<>();
        for (Expression vertex : vertices) {
            edges.put(vertex, new ArrayList<>());
        }
        specialTaggedEdges = new HashMap<>();
    }

    public void addEdge(Expression from, Expression to) {
        ArrayList<Expression> routes = edges.get(from);
        if (!routes.contains(to)) {
            routes.add(to);
        }
    }

    public boolean isTagged(Expression from, Expression to) {
        return specialTaggedEdges.containsKey(from) && specialTaggedEdges.get(from).equals(to);
    }

    public Expression[] getRouteFrom(Expression one) {
        return edges.get(one).toArray(new Expression[0]);
    }

    public Expression[] getRouteTo(Expression one) {
        ArrayList<Expression> route = new ArrayList<>();
        for (Expression expr : edges.keySet()) {
            if (edges.get(expr).contains(one)) {
                route.add(expr);
            }
        }
        return route.toArray(new Expression[0]);
    }

    public Expression[] getVertices() {
        return edges.keySet().toArray(new Expression[0]);
    }

    public ExpressionDAG[] weaklyConnectedComponents() {
        Expression[] vertices = getVertices();
        ExpressionDAG undirectedGraph = new ExpressionDAG(vertices);
        for (Expression u : undirectedGraph.getVertices()) {
            for (Expression v : getRouteFrom(u)) {
                undirectedGraph.addEdge(u, v);
                undirectedGraph.addEdge(v, u);
            }
        }
        return connectedComponents(undirectedGraph);
    }

    private ExpressionDAG[] connectedComponents(ExpressionDAG undirectedGraph)
    {
        Expression[] vertices = undirectedGraph.getVertices();
        ArrayList<ExpressionDAG> connectedComponents = new ArrayList<>();
        HashMap<Expression, Boolean> isVisited = new HashMap<>();
        for (Expression expr : vertices) {
            isVisited.put(expr, false);
        }

        for (Expression expr : vertices) {
            if (!isVisited.get(expr)) {
                ArrayList<Expression> component = new ArrayList<>();
                findConnectedComponent(expr, isVisited,
                        component,
                        undirectedGraph);
                ExpressionDAG exprDag = new ExpressionDAG(component.toArray(new Expression[0]));
                for (Expression i : component) {
                    for (Expression j : edges.get(i)) {
                        if (!isTagged(i, j)) {
                            exprDag.addEdge(i, j);
                        } else {
                            exprDag.addTaggedEdge(i, j);
                        }
                    }
                }
                connectedComponents.add(exprDag);
            }
        }

        return connectedComponents.toArray(new ExpressionDAG[0]);
    }

    public void addTaggedEdge(Expression i, Expression j) {
        addEdge(i, j);
        specialTaggedEdges.put(i, j);
    }

    private void findConnectedComponent(Expression src, HashMap<Expression, Boolean> isVisited, ArrayList<Expression> component, ExpressionDAG undirectedGraph)
    {
        isVisited.put(src, true);
        component.add(src);

        for (Expression v : undirectedGraph.getRouteFrom(src))
            if (!isVisited.get(v))
                findConnectedComponent(v, isVisited, component, undirectedGraph);
    }

    public List<Expression> findLongestPath() {
        List<Expression> topologicalOrder = topologicalSort();
        Map<Expression, Integer> distances = new HashMap<>();
        Map<Expression, Expression> predecessors = new HashMap<>();

        for (Expression vertex : edges.keySet()) {
            distances.put(vertex, Integer.MIN_VALUE);
        }

        for (Expression vertex : topologicalOrder) {
            if (distances.get(vertex) == Integer.MIN_VALUE) {
                distances.put(vertex, 0);
            }

            for (Expression neighbor : edges.get(vertex)) {
                if (distances.get(neighbor) < distances.get(vertex) + 1) {
                    distances.put(neighbor, distances.get(vertex) + 1);
                    predecessors.put(neighbor, vertex);
                }
            }
        }

        Expression maxDistanceVertex = null;
        int maxDistance = Integer.MIN_VALUE;
        for (Map.Entry<Expression, Integer> entry : distances.entrySet()) {
            if (entry.getValue() > maxDistance) {
                maxDistance = entry.getValue();
                maxDistanceVertex = entry.getKey();
            }
        }

        List<Expression> longestPath = new ArrayList<>();
        for (Expression at = maxDistanceVertex; at != null; at = predecessors.get(at)) {
            longestPath.add(at);
        }
        Collections.reverse(longestPath);
        return longestPath;
    }

    private List<Expression> topologicalSort() {
        Set<Expression> visited = new HashSet<>();
        Stack<Expression> stack = new Stack<>();
        for (Expression vertex : edges.keySet()) {
            if (!visited.contains(vertex)) {
                topologicalSortUtil(vertex, visited, stack);
            }
        }
        List<Expression> topologicalOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            topologicalOrder.add(stack.pop());
        }
        return topologicalOrder;
    }

    private void topologicalSortUtil(Expression vertex, Set<Expression> visited, Stack<Expression> stack) {
        visited.add(vertex);
        for (Expression neighbor : edges.get(vertex)) {
            if (!visited.contains(neighbor)) {
                topologicalSortUtil(neighbor, visited, stack);
            }
        }
        stack.push(vertex);
    }

    public Expression getVertexWithMinOutgoingEdges() {
        Expression minVertex = null;
        int minEdges = Integer.MAX_VALUE;
        for (Map.Entry<Expression, ArrayList<Expression>> entry : edges.entrySet()) {
            int outDegree = entry.getValue().size();
            if (outDegree < minEdges) {
                minEdges = outDegree;
                minVertex = entry.getKey();
            }
        }
        return minVertex;
    }

    public Expression getVertexWithMinIncomingEdges() {
        Map<Expression, Integer> inDegreeMap = new HashMap<>();
        for (Expression vertex : edges.keySet()) {
            inDegreeMap.put(vertex, 0);
        }
        for (ArrayList<Expression> adjList : edges.values()) {
            for (Expression neighbor : adjList) {
                inDegreeMap.put(neighbor, inDegreeMap.get(neighbor) + 1);
            }
        }
        Expression minVertex = null;
        int minEdges = Integer.MAX_VALUE;
        for (Map.Entry<Expression, Integer> entry : inDegreeMap.entrySet()) {
            if (entry.getValue() < minEdges) {
                minEdges = entry.getValue();
                minVertex = entry.getKey();
            }
        }
        return minVertex;
    }

    public Expression getVertexWithMaxOutgoingEdges() {
        Expression maxVertex = null;
        int maxEdges = Integer.MIN_VALUE;
        for (Map.Entry<Expression, ArrayList<Expression>> entry : edges.entrySet()) {
            int outDegree = entry.getValue().size();
            if (outDegree > maxEdges) {
                maxEdges = outDegree;
                maxVertex = entry.getKey();
            }
        }
        return maxVertex;
    }

    public Expression getVertexWithMaxIncomingEdges() {
        Map<Expression, Integer> inDegreeMap = new HashMap<>();
        for (Expression vertex : edges.keySet()) {
            inDegreeMap.put(vertex, 0);
        }
        for (ArrayList<Expression> adjList : edges.values()) {
            for (Expression neighbor : adjList) {
                inDegreeMap.put(neighbor, inDegreeMap.get(neighbor) + 1);
            }
        }
        Expression maxVertex = null;
        int maxEdges = Integer.MIN_VALUE;
        for (Map.Entry<Expression, Integer> entry : inDegreeMap.entrySet()) {
            if (entry.getValue() > maxEdges) {
                maxEdges = entry.getValue();
                maxVertex = entry.getKey();
            }
        }
        return maxVertex;
    }

    public void removeEdge(Expression u, Expression v) {
        edges.get(u).remove(v);
    }


    public void removeOrphanedVertices() {
        List<Expression> toDelete = new ArrayList<>();
        for (Expression expr : edges.keySet()) {
            if (getRouteFrom(expr).length == 0 && getRouteTo(expr).length == 0) {
                toDelete.add(expr);
            }
        }
        for (Expression del : toDelete) {
            edges.remove(del);
        }
    }

}