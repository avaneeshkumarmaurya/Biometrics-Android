package com.notbytes.biometrics_android;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class FingerprintActivity extends AppCompatActivity implements View.OnClickListener, FingerprintAuthenticationCallback {
    private static final int REQUEST_FINGERPRINT_PERMISSION = 344;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        textView = findViewById(R.id.tv_desc);
        findViewById(R.id.btn_authenticate).setOnClickListener(this);
//        requestPermissions(new String[]{Manifest.permission.USE_BIOMETRIC}, REQUEST_FINGERPRINT_PERMISSION);
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintHandler handler = new FingerprintHandler(this, this);
            try {
                handler.startAuth();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void permissionNotAvailable() {
        update("Permission not available", false);
    }

    @Override
    public void hardwareNotAvailable() {
        update("Hardware not available", false);
    }

    @Override
    public void onAuthenticationCancelled() {
        update("Authentication Cancelled", false);
    }

    @Override
    public void onSetupComplete() {
        textView.setText(R.string.place_fingerprint_help);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        update("Fingerprint Authentication error\n" + errString, false);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        update("Fingerprint Authentication help\n" + helpString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        update("Fingerprint Authentication failed.", false);
    }

    @Override
    public void onAuthenticationSucceeded() {
        update("Fingerprint Authentication succeeded.", true);
    }

    private void update(String e, Boolean success) {
        textView.setText(e);
        if (success) {
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
    }
}