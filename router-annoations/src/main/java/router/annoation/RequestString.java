package router.annoation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by DingZhu on 2017/9/6.
 *
 * @since 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(CLASS)
public @interface RequestString {

    String[] value();
}
