package router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

    public IntentWrapper addParam(String extraKey, boolean value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, char value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, byte value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, short value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, int value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, long value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, float value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, double value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, String value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, Parcelable value) {
        intent.putExtra(extraKey, value);
        return this;
    }


    public IntentWrapper addParam(String extraKey, boolean[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, char[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, byte[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, short[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, int[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, long[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, float[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, double[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, String[] value) {
        intent.putExtra(extraKey, value);
        return this;
    }

    public IntentWrapper addParam(String extraKey, Parcelable[] value) {
        intent.putExtra(extraKey, value);
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
