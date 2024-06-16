package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.comparison.BinaryComparison;
import org.vstu.meaningtree.nodes.literals.BoolLiteral;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.statements.*;

import java.util.Arrays;
import java.util.List;

public class PythonSpecialTreeTransformations {

    public static Node representGeneralFor(GeneralForLoop generalFor) {
        return null;
    }

    public static Node representDoWhile(DoWhileLoop doWhile) {
        Expression condition = doWhile.getCondition();
        if (condition instanceof BinaryComparison cmp) {
            condition = cmp.inverse();
        } else {
            condition = new NotOp(new ParenthesizedExpression(condition));
        }
        IfStatement breakCondition = new IfStatement(condition, new BreakStatement(), null);
        List<Node> body = Arrays.asList(doWhile.getBody().getNodes());
        body.add(breakCondition);
        return new WhileLoop(new BoolLiteral(true), new CompoundStatement(body));
    }


    /*
    TODO:
    - convert assignment statement of first variable usage to variable declaration
     */
}
