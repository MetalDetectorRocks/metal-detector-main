package rocks.metaldetector.config.constants;

public class MessageKeys {

  public static class Registration {
    public static final String SUCCESS = "registration.success";
  }

  public static class ForgotPassword {
    public static final String SUCCESS               = "forgot-password.success";
    public static final String USER_DOES_NOT_EXIST   = "forgot-password.user-does-not-exist";
    public static final String TOKEN_DOES_NOT_EXIST  = "forgot-password.token-does-not-exist";
    public static final String TOKEN_IS_EXPIRED      = "forgot-password.token-is-expired";
  }

}
