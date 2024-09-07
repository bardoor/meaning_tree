package org.vstu.meaningtree.languages.utils;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.FunctionCall;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.io.InputCommand;
import org.vstu.meaningtree.nodes.io.PrintCommand;


public class PythonSpecificFeatures {

    static SimpleIdentifier getSpecificFunctionName(FunctionCall call) {
        if (call instanceof PrintCommand) {
            return new SimpleIdentifier("print");
        } else if (call instanceof InputCommand) {
            return new SimpleIdentifier("input");
        }
        return null;
    }

    public static Identifier getFunctionName(FunctionCall call) {
        SimpleIdentifier specific = getSpecificFunctionName(call);
        if (specific != null) {
            return specific;
        }
        return call.getFunctionName();
    }

    public static Expression getFunctionExpression(FunctionCall call) {
        SimpleIdentifier specific = getSpecificFunctionName(call);
        if (specific != null) {
            return specific;
        }
        return call.getFunction();
    }
}
