package test.technical.librarian.utils;

import java.lang.annotation.*;

/**
 * Indicates a field as a differentiator field. <p> A differentiator field is a field alongside @Id
 * field in a model class that is used in its equals() and hashCode() implementation. <p> Usage of
 * this annotation is needed if you want to use <code>OneToManyUtils</code> class.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Differentiator {

}
