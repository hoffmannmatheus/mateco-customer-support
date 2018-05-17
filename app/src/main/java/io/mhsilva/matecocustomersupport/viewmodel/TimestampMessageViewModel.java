package io.mhsilva.matecocustomersupport.viewmodel;

import android.databinding.BaseObservable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampMessageViewModel extends BaseObservable {

    private long mTimestamp;

    public TimestampMessageViewModel(long timestamp) {
        mTimestamp = timestamp;
    }

    public String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("KK:mm aa", new Locale("en-us"));
        return df.format(new Date(mTimestamp));
    }
}
