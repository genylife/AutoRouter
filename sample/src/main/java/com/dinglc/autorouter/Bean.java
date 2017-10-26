package com.dinglc.autorouter;

/**
 * Created by DingZhu on 2017/9/30.
 *
 * @since 1.0.0
 */

public class Bean {

    public String ert;

    public Bean(){}
    public Bean(String ert) {
        this.ert = ert;

    }

    @Override public String toString() {
        return "ert: " + ert;
    }

}
