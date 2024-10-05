package org.vstu.meaningtree.nodes.expressions.pointers;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.other.MemberAccess;

public class PointerMemberAccess extends MemberAccess {
    public PointerMemberAccess(Expression expr, SimpleIdentifier member) {
        super(expr, member);
    }
}
