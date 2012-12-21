package org.xmlbeam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the projection function for elements of a projection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Xpath {

    /**
     * XPath expression to project XML data to return type of decorated method.
     *
     * @return
     */
    String value();

    /**
     * Type of desired collection content. My be omitted for arrays.
     * @return
     */
    Class<?> targetComponentType() default Xpath.class;
}
