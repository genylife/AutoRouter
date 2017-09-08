package router.annoation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.CLASS;

@Documented
@Target(ElementType.TYPE)
@Retention(CLASS)
public @interface RequestParcelable {

    String[] value();
}
