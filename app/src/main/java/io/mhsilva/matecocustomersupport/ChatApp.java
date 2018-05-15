package io.mhsilva.matecocustomersupport;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

/**
 * The main app class.
 */
public class ChatApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // only allow crashlytics if not debug.
        Fabric.with(this, new Crashlytics.Builder().core(
                new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /*
         * This was added because the app exceeded the 64K method count, which would cause
         * random NoClassDefFoundException errors randomly on older devices.
         * The MultiDex will allow older devices to support a greater number of methods.
         */
        MultiDex.install(this);
    }
}
