package router;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;

public final class IntentWrapper {

    private Intent intent;
    private Activity context;
    private ActivityOptionsCompat options;

    IntentWrapper(Activity context, Intent intent) {
        this.intent = intent;
        this.context = context;
    }

    public IntentWrapper withOptionsCompat(ActivityOptionsCompat options) {
        this.options = options;
        return this;
    }

    public IntentWrapper addFlags(int flag) {
        intent.addFlags(flag);
        return this;
    }

    public void go() {
        context.startActivityForResult(intent, -1);
    }

    public void go(int requestCode) {
        context.startActivityForResult(intent, requestCode);
    }
}
