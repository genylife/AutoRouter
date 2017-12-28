package com.dinglc.autorouter;

import android.os.Parcel;
import android.os.Parcelable;

public class Bean implements Parcelable {

    public String ert;

    public Bean(){}
    public Bean(String ert) {
        this.ert = ert;

    }

    @Override public String toString() {
        return "ert: " + ert;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ert);
    }

    protected Bean(Parcel in) {
        this.ert = in.readString();
    }

    public static final Parcelable.Creator<Bean> CREATOR = new Parcelable.Creator<Bean>() {
        @Override
        public Bean createFromParcel(Parcel source) {
            return new Bean(source);
        }

        @Override
        public Bean[] newArray(int size) {
            return new Bean[size];
        }
    };
}
