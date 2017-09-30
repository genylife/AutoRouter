package router.processors;

import java.util.List;

/**
 * Created by DingZhu on 2017/9/22.
 *
 * @since 1.0.0
 */

final class RouterElement {

    private String value;
    private List<ExtraElement> extraElement;

    RouterElement(String value) {
        this.value = value;
    }

    String getValue() {
        return value;
    }

    public List<ExtraElement> getExtraElement() {
        return extraElement;
    }

    public void setExtraElement(List<ExtraElement> extraElement) {
        this.extraElement = extraElement;
    }
}
