package io.mhsilva.matecocustomersupport;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;
import io.mhsilva.matecocustomersupport.manager.ChatServerManager;
import io.mhsilva.matecocustomersupport.manager.SessionManager;

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

        SessionManager.initialize(getApplicationContext());
        ChatServerManager.initialize(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /*
         * If this app exceeds the 64K method count, it would cause
         * random NoClassDefFoundException errors randomly on older devices.
         * The MultiDex will allow older devices to support a greater number of methods.
         */
        MultiDex.install(this);
    }
}
