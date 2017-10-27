package router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

public final class IntentWrapper {

    private Intent intent;
    private Context context;
    private ActivityOptionsCompat options;

    IntentWrapper(Context context, Intent intent) {
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
        Bundle bundle = null;
        if(options != null) {
            bundle = options.toBundle();
        }
        ActivityCompat.startActivity(context, intent, bundle);
    }

    public void go(int requestCode) {
        Bundle bundle = null;
        if(options != null) {
            bundle = options.toBundle();
        }
        if(context instanceof Activity) {
            ActivityCompat.startActivityForResult(((Activity) context), intent, requestCode, bundle);
        } else {
            go();
        }
    }
}
