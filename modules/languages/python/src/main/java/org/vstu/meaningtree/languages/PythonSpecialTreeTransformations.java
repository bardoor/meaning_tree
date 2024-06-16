package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.comparison.BinaryComparison;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.VariableDeclarator;
import org.vstu.meaningtree.nodes.literals.BoolLiteral;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.statements.*;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PythonSpecialTreeTransformations {

    public static Node[] representGeneralFor(GeneralForLoop generalFor) {
        boolean needDeleting = false;
        HasInitialization initializer = null;
        if (generalFor.hasInitializer()) {
            initializer = generalFor.getInitializer();
            needDeleting = initializer instanceof VariableDeclaration;
        }

        Expression condition = new BoolLiteral(true);
        if (generalFor.hasCondition()) {
            condition = generalFor.getCondition();
        }
        Expression update = null;
        if (generalFor.hasUpdate()) {
            update = generalFor.getUpdate();
        }
        Statement stmt = generalFor.getBody();
        CompoundStatement body;
        if (stmt instanceof CompoundStatement compound) {
            body = new CompoundStatement(Arrays.asList(compound.getNodes()));
        } else {
            body = new CompoundStatement(stmt);
        }
        _prepend_continue_with_expression(body, update);
        body.insert(body.getLength(), update);
        List<Node> result = new ArrayList<>();
        result.add((Node) initializer);
        result.add(new WhileLoop(condition, body));
        if (needDeleting) {
            VariableDeclaration varDecl = (VariableDeclaration) initializer;
            for (VariableDeclarator declarator : varDecl.getDeclarators()) {
                result.add(new DeleteStatement(declarator.getIdentifier()));
            }
        }
        return new Node[] {(Node)initializer, new WhileLoop(condition, body)};
    }

    private static void _prepend_continue_with_expression(CompoundStatement compound, Expression update) {
        int found = -1;
        Node[] nodes = compound.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof ContinueStatement) {
                found = i;
            }
        }
        if (found != -1) {
            compound.insert(found, update);
        }
        for (Node node : compound) {
            if (node instanceof ForLoop || node instanceof WhileLoop) {
                return;
            } else if (node instanceof IfStatement ifStmt) {
                ifStmt.makeBodyCompound();
                for (ConditionBranch branch : ifStmt.getBranches()) {
                    _prepend_continue_with_expression((CompoundStatement) branch.getBody(), update);
                }
                if (ifStmt.hasElseBranch()) {
                    _prepend_continue_with_expression((CompoundStatement) ifStmt.getElseBranch(), update);
                }
            } else if (node instanceof SwitchStatement switchStmt) {
                //TODO: it is correct (continue usage) in programming languages?
                switchStmt.makeBodyCompound();
                for (ConditionBranch branch : switchStmt.getCases()) {
                    _prepend_continue_with_expression((CompoundStatement) branch.getBody(), update);
                }
                if (switchStmt.hasDefaultCase()) {
                    _prepend_continue_with_expression((CompoundStatement) switchStmt.getDefaultCase(), update);
                }
            } else if (node instanceof HasBodyStatement hasBodyStmt) {
                hasBodyStmt.makeBodyCompound();
                _prepend_continue_with_expression((CompoundStatement) hasBodyStmt.getBody(), update);
            }
        }
    }

    public static Node representDoWhile(DoWhileLoop doWhile) {
        Expression condition = doWhile.getCondition();
        if (condition instanceof BinaryComparison cmp) {
            condition = cmp.inverse();
        } else {
            condition = new NotOp(new ParenthesizedExpression(condition));
        }
        IfStatement breakCondition = new IfStatement(condition, new BreakStatement(), null);
        List<Node> body;
        if (doWhile.getBody() instanceof CompoundStatement compound) {
            body = Arrays.asList(compound.getNodes());
        } else {
            body = new ArrayList<>();
            body.add(doWhile.getBody());
        }
        body.add(breakCondition);
        return new WhileLoop(new BoolLiteral(true), new CompoundStatement(body));
    }


    /*
    TODO:
    - convert assignment statement of first variable usage to variable declaration
     */
}
