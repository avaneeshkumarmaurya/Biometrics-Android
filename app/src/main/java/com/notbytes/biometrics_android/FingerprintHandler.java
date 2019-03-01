package com.notbytes.biometrics_android;


import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import java.security.KeyStore;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler implements Executor, DialogInterface.OnClickListener {
    private static final String KEY_NAME = "com.notbytes.biometrics_android_p.avaneesh";
    private Cipher cipher;
    private KeyStore keyStore;
    private Context context;
    private FingerprintAuthenticationCallback mCallback;

    public FingerprintHandler(Context mContext, FingerprintAuthenticationCallback callback) {
        context = mContext;
        this.mCallback = callback;
    }


    public void startAuth() throws Exception {
        if (context == null) {
            throw new Exception("Context should not be null");
        }
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);

        if (keyguardManager == null) {
            throw new Exception("Keyguard manager should not be null");
        }
        if (fingerprintManager == null) {
            throw new Exception("FingerprintManager should not be null");
        }
        if (mCallback == null) {
            throw new Exception("FingerprintAuthenticationCallback should not be null");
        }
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
                mCallback.permissionNotAvailable();
                return;
            }
            PackageManager packageManager = context.getPackageManager();
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                mCallback.hardwareNotAvailable();
                return;
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                mCallback.onAuthenticationHelp(-1, "No fingerprints are enrolled");
                return;
            }
            BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                    .setTitle("Fingerprint")
                    .setSubtitle("Testing Fingerprint")
                    .setDescription("Please touch your biometrics")
                    .setNegativeButton("Cancel", this, this).build();
            mCallback.onSetupComplete();
            biometricPrompt.authenticate(cancellationSignal, this, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if (mCallback != null) {
                        mCallback.onAuthenticationError(errorCode, errString);
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    if (mCallback != null) {
                        mCallback.onAuthenticationHelp(helpCode, helpString);
                    }
                }

                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    if (mCallback != null) {
                        mCallback.onAuthenticationSucceeded();
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    if (mCallback != null) {
                        mCallback.onAuthenticationFailed();
                    }
                }
            });

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                mCallback.permissionNotAvailable();
                return;
            }
            if (!fingerprintManager.isHardwareDetected()) {
                mCallback.hardwareNotAvailable();
                return;
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                mCallback.onAuthenticationHelp(-1, "No fingerprints are enrolled");
                return;
            }
            if (!keyguardManager.isKeyguardSecure()) {
                mCallback.onAuthenticationHelp(-1, "Keyguard is not enabled");
                return;
            }
            mCallback.onSetupComplete();
            generateKey();
            if (cipherInit()) {
                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (mCallback != null) {
                            mCallback.onAuthenticationError(errorCode, errString);
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        if (mCallback != null) {
                            mCallback.onAuthenticationHelp(helpCode, helpString);
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        if (mCallback != null) {
                            mCallback.onAuthenticationSucceeded();
                        }

                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        if (mCallback != null) {
                            mCallback.onAuthenticationFailed();
                        }

                    }
                }, null);
            }
        }
    }

    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            return false;
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception ignored) {

        }
        if (keyGenerator == null) {
            return;
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void execute(@NonNull Runnable command) {
//        mCallback.onAuthenticationSucceeded();
//        if (mCallback != null) {
//            mCallback.onAuthenticationCancelled();
//        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
            mCallback.onAuthenticationCancelled();
        }
    }
}