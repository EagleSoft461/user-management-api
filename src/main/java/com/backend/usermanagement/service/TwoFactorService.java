package com.backend.usermanagement.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class TwoFactorService {

    // Yeni bir TOTP secret key üret (kullanıcıya özel)
    public String generateSecret() {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        return secretGenerator.generate();
    }

    // QR code üret — Google Authenticator bu QR'ı tarar
    // Base64 encoded PNG olarak döner (data URI)
    public String generateQrCodeDataUri(String email, String secret) throws QrGenerationException {
        QrData data = new QrData.Builder()
                .label(email)                          // Authenticator'da görünecek isim
                .secret(secret)                        // TOTP secret
                .issuer("UserManagementAPI")           // Uygulama adı
                .algorithm(HashingAlgorithm.SHA1)      // TOTP algoritması
                .digits(6)                             // 6 haneli kod
                .period(30)                            // 30 saniyede bir yenilenir
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = generator.generate(data);
        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

    // Kullanıcının girdiği 6 haneli kodu doğrula
    public boolean verifyCode(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secret, code);
    }
}
