package router.processors;

import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * Created by wanqi on 2017/9/11.
 *
 * @since 1.0.0
 */

public class InjectClassElement {

    private TypeElement mClassElement;
    private List<InjectElement> mElementList;

    public TypeElement getClassElement() {
        return mClassElement;
    }

    public void setClassElement(TypeElement aClass) {
        mClassElement = aClass;
    }

    public List<InjectElement> getElementList() {
        return mElementList;
    }

    public void setElementList(List<InjectElement> elementList) {
        mElementList = elementList;
    }
}
