Biometrics-Android-P
===================
* Reefrences-
https://android-developers.googleblog.com/2018/06/better-biometrics-in-android-p.html

Step 1: Add the required permissions in the AndroidManifest.xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.an.biometric">

    <!-- Step 1: Add the following permission to the app  -->
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />

    <!-- Step 2: This permission is depreciated in Android P  -->
    <uses-permission android:name="android.permission.USE_FINGERPRINT" />

</manifest>
