package io.mhsilva.matecocustomersupport.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.mhsilva.matecocustomersupport.R;
import io.mhsilva.matecocustomersupport.activity.ChatActivity;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_startup);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startActivity(ChatActivity.newInstance(this));
    }
}
