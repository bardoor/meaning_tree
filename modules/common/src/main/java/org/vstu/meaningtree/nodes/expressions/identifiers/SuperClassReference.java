package org.vstu.meaningtree.nodes.expressions.identifiers;

public class SuperClassReference extends SimpleIdentifier {
    public SuperClassReference() {
        super("super");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SuperClassReference)) {
            return false;
        }
        return super.equals(o);
    }
}
