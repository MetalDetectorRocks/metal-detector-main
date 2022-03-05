package rocks.metaldetector.service.token;

public interface TokenService {

  String createEmailVerificationToken(String publicUserId);

  String createResetPasswordToken(String publicUserId);

  void resendExpiredEmailVerificationToken(String tokenString);

  void verifyEmailToken(String tokenString);
}
