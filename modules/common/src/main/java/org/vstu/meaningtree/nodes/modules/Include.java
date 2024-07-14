package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.Identifier;

public class Include extends Import {
    public enum IncludeType {
        // #include "defs.h"
        QUOTED_FORM,
        // #include <stdio.h>
        POINTY_BRACKETS_FORM,
    }

    private final IncludeType _includeType;

    public Include(Identifier scope, IncludeType includeType) {
        super(scope);
        _includeType = includeType;
    }

    public IncludeType getIncludeType() {
        return _includeType;
    }
}
