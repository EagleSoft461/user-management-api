package com.backend.usermanagement.dto.response;

public class TwoFactorSetupResponse {

    private String secret;
    private String qrCodeDataUri;  // Base64 PNG — tarayıcıda gösterilebilir

    public TwoFactorSetupResponse(String secret, String qrCodeDataUri) {
        this.secret = secret;
        this.qrCodeDataUri = qrCodeDataUri;
    }

    public String getSecret() { return secret; }
    public String getQrCodeDataUri() { return qrCodeDataUri; }
}
