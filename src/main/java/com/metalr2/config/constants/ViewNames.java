package com.metalr2.config.constants;

public class ViewNames {

  // Authentication
  public static final String INDEX                     = "guest/index";
  public static final String LOGIN                     = "guest/auth/login";
  public static final String REGISTER                  = "guest/auth/register";
  public static final String FORGOT_PASSWORD           = "guest/auth/forgot-password";
  public static final String RESET_PASSWORD            = "guest/auth/reset-password";

  // Admin area
  public static final String USERS_LIST                = "backend/users/list";
  public static final String USERS_CREATE              = "backend/users/create";
  public static final String USERS_EDIT                = "backend/users/edit";

  // Error pages
  public static final String ERROR                     = "error/default";
  public static final String ERROR_404                 = "error/404";
  public static final String ERROR_500                 = "error/500";

  public static class EmailTemplates {
    public static final String REGISTRATION_VERIFICATION = "email/registration-verification-email";
    public static final String FORGOT_PASSWORD           = "email/forgot-password-email";
  }

}
