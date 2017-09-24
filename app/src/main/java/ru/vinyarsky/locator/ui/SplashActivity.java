package ru.vinyarsky.locator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.vinyarsky.ui.R;

public final class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent();
            mainIntent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, 3000);
    }
}
