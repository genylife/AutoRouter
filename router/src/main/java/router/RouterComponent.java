package router;


import android.content.Context;
import android.content.Intent;

public class RouterComponent {

    private Context mContext;

    private RouterComponent() {
    }

    private RouterComponent(Context context) {
        mContext = context;
    }

    public static RouterComponent with(Context context) {
        return new RouterComponent(context);
    }


    public IntentWrapper name(String name) {
        Class toActivity = null;
        try {
            toActivity = RouterServiceManager.find(name);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(mContext, toActivity);

        return new IntentWrapper(mContext, intent);
    }
}
