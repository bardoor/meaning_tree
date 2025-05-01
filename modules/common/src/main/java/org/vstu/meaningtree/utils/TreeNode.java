package org.vstu.meaningtree.utils;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TreeNode {
    enum Type {
       DEFAULT,
       MAP_WITH_NODE_VALUE,
       MAP_WITH_NODE_KEY, MAP_NODE_KEY_VALUE
    }

    boolean readOnly() default false;
    String alias() default "";
    Type type() default Type.DEFAULT;
}
