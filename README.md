Fingerprint/Biometrics-Android Marshmallow(M) and Android Pie(P)
Starting from marshmallow android supports fingerprint sensor.
Using Fingerprint api we can authenticate faster, but this is less secure that pin and passwords.
Android done change in the fingerprint api and provide Biometrics from android Pie.
Now we can use Biometrics Api in easy way.
Below code snippet is able to create a biometric prompt Object.
BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
        .setTitle("Fingerprint")
        .setSubtitle("Testing Fingerprint")
        .setDescription("Please touch your biometrics")
        .setNegativeButton("Cancel", this, this).build();
After creating biometric prompt we have to call authenticate method.
biometricPrompt.authenticate(cancellationSignal, this, new BiometricPrompt.AuthenticationCallback() {
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
    }
});
When we call authenticate method then android show a dialogue.
