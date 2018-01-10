package router;


import android.content.Context;
import android.content.Intent;

public class RouterComponent {

    private Context mContext;
    private String mName;

    private RouterComponent(Context context) {
        mContext = context;
    }

    public static RouterComponent with(Context context) {
        return new RouterComponent(context);
    }


    public IntentWrapper name(String name) {
        mName = name;
        //通过name找到toActivity（通过RouterServiceManager去查找）,并返回IntentWrapper

        Intent intent = new Intent(/*mContext,toActivity*/);
        IntentWrapper wrapper = new IntentWrapper(mContext, intent);

        return wrapper;
    }
}
