package org.vstu.meaningtree.utils.env.records;

import org.vstu.meaningtree.nodes.interfaces.HasSymbolScope;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;
import org.vstu.meaningtree.utils.env.SymbolRecord;


public class UserTypeRecord extends SymbolRecord implements HasSymbolScope {
    protected SymbolEnvironment _env;

    protected UserTypeRecord(SymbolEnvironment parent, int declarationPosition) {
        super(declarationPosition);
        _env = new SymbolEnvironment(parent);
    }

    @Override
    public SymbolEnvironment getEnv() {
        return _env;
    }
}
