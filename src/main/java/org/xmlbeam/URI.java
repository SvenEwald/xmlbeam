package org.xmlbeam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind the decorated projection to a XML document location. When used on methods, an implicit projection is
 * created when the method is invoked.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface URI {


    String value();

}
