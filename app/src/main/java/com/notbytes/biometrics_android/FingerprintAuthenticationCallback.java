package com.notbytes.biometrics_android;

/**
 * Created by Avaneesh Maurya on 01,March,2019
 */
public interface FingerprintAuthenticationCallback {

    void permissionNotAvailable();

    void hardwareNotAvailable();

    void onAuthenticationCancelled();

    void onSetupComplete();

    void onAuthenticationError(int errMsgId, CharSequence errString);

    void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

    void onAuthenticationFailed();

    void onAuthenticationSucceeded();
}
