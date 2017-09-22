package router.processors;

import router.RouterType;

/**
 * Created by DingZhu on 2017/9/22.
 *
 * @since 1.0.0
 */

final class RouterElement {

    private String value;
    private RouterType type;

    RouterElement(String value, RouterType type) {
        this.value = value;
        this.type = type;
    }

    String getValue() {
        return value;
    }

    RouterType getType() {
        return type;
    }
}
