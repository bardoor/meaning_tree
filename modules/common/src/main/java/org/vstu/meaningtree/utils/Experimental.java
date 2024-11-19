package org.vstu.meaningtree.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD,
        ElementType.PACKAGE, ElementType.MODULE, ElementType.RECORD_COMPONENT,
        ElementType.CONSTRUCTOR
})
public @interface Experimental {
}
