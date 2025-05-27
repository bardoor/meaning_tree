package org.vstu.meaningtree.iterators.utils;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TreeNode {
    boolean readOnly() default false;
    String alias() default "";
}
