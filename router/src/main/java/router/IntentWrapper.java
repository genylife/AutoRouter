package router;

import android.app.Activity;
import android.content.Intent;

public final class IntentWrapper {

    private Intent intent;
    private Activity context;

    IntentWrapper(Activity context, Intent intent) {
        this.intent = intent;
        this.context = context;
    }

    public void addFlags(int flag) {
        intent.addFlags(flag);
    }

    public void go() {
        context.startActivity(intent);
    }

    public void goForResult(int requestCode) {
        context.startActivityForResult(intent, requestCode);
    }
}
