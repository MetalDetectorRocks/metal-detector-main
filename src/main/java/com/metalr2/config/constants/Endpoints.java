package com.metalr2.config.constants;

public class Endpoints {

  public static class Guest {
    // General
    public static final String INDEX       = "/index";
    public static final String EMPTY_INDEX = "";
    public static final String SLASH_INDEX = "/";

    // Authentication
    public static final String LOGIN                     = "/login";
    public static final String REGISTER                  = "/register";
    public static final String REGISTRATION_VERIFICATION = "/registration-verification";
    public static final String RESEND_VERIFICATION_TOKEN = "/resend-verification-token";
    public static final String FORGOT_PASSWORD           = "/forgot-password";
    public static final String RESET_PASSWORD            = "/reset-password";
    public static final String LOGOUT                    = "/logout";

    // Errors
    public static final String ERROR       = "/error";
  }

  public static class Frontend {
    public static final String FOLLOW_ARTISTS        = "/follow-artists";
    public static final String SETTINGS              = "/settings";
    public static final String PROFILE               = "/profile";
    public static final String ARTISTS_RELEASES      = "/artists-releases";
    public static final String ALL_RELEASES          = "/all-releases";
    public static final String MY_ARTISTS            = "/my-artists";
    public static final String REPORT_ARTIST_RELEASE = "/report-artist-release";
    public static final String ABOUT                 = "/about";
    public static final String TEAM                  = "/team";
    public static final String CONTACT               = "/contact";
    public static final String IMPRINT               = "/imprint";
  }

  public static class AdminArea {
    // Users
    public static final String USERS_LIST   = "/admin/users";
    public static final String USERS_CREATE = "/admin/users/create";
    public static final String USERS_EDIT   = "/admin/users/edit/{id}";
  }


  public static class AntPattern {
    public static final String   ADMIN                     = "/admin/**";
    public static final String[] INDEX                     = {"/", "/index", "/index/"};
    public static final String[] LOGIN                     = {"/login", "/login/"};
    public static final String[] REGISTER                  = {"/register", "/register/"};
    public static final String[] REGISTRATION_VERIFICATION = {"/registration-verification", "/registration-verification/"};
    public static final String[] RESEND_VERIFICATION_TOKEN = {"/resend-verification-token", "/resend-verification-token/"};
    public static final String[] FORGOT_PASSWORD           = {"/forgot-password", "/forgot-password/"};
    public static final String[] RESET_PASSWORD            = {"/reset-password", "/reset-password/"};
    public static final String[] RESOURCES                 = {"/resources/**", "/css/**", "/js/**", "/images/**"};
  }

}
