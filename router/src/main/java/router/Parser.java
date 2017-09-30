package router;

/**
 * Created by DingZhu on 2017/9/30.
 *
 * @since 1.0.0
 */

public interface Parser {

    <T> T parse(String text, Class<T> clazz);
}
