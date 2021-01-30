package rocks.metaldetector.config.constants;

public class ViewNames {

  public static class Guest {
    // Authentication
    public static final String INDEX              = "guest/index";
    public static final String LOGIN              = "guest/auth/login";
    public static final String REGISTER           = "guest/auth/register";
    public static final String DISABLED_REGISTER  = "guest/auth/disabled-register";
    public static final String FORGOT_PASSWORD    = "guest/auth/forgot-password";
    public static final String RESET_PASSWORD     = "guest/auth/reset-password";
    public static final String IMPRINT            = "guest/imprint";
    public static final String PRIVACY_POLICY     = "guest/privacy-policy";

    // Error pages
    public static final String ERROR       = "error/default";
    public static final String ERROR_400   = "error/400";
    public static final String ERROR_403   = "error/403";
    public static final String ERROR_404   = "error/404";
    public static final String ERROR_500   = "error/500";
  }

  public static class Frontend {
    public static final String HOME                    = "frontend/home";
    public static final String SEARCH                  = "frontend/search";
    public static final String RELEASES                = "frontend/releases";
    public static final String BLOG                    = "frontend/blog";
    public static final String MY_ARTISTS              = "frontend/my-artists";
    public static final String IMPRINT                 = "frontend/imprint";
    public static final String PRIVACY_POLICY          = "frontend/privacy-policy";
    public static final String ACCOUNT_DETAILS         = "frontend/account-details";
    public static final String SPOTIFY_SYNCHRONIZATION = "frontend/spotify-synchronization";
    public static final String NOTIFICATION_SETTINGS   = "frontend/notification-settings";
    public static final String STATUS                  = "frontend/status";
  }

  public static class AdminArea {
    public static final String INDEX       = "admin/index";
    public static final String IMPORT      = "admin/import/list";
    public static final String ANALYTICS   = "admin/analytics";
    public static final String SETTINGS    = "admin/settings";
    public static final String RELEASES    = "admin/releases/list";
    public static final String USERS       = "admin/users/list";
    public static final String PROFILE     = "admin/profile";
  }

  public static class EmailTemplates {
    public static final String REGISTRATION_VERIFICATION = "email/registration-verification-email";
    public static final String FORGOT_PASSWORD           = "email/forgot-password-email";
    public static final String NEW_RELEASES              = "email/releases-email";
    public static final String TODAYS_RELEASES           = "email/todays-releases-email";
    public static final String TODAYS_ANNOUNCEMENTS      = "email/todays-announcements-email";
    public static final String ACCOUNT_DELETED           = "email/account-deleted-email";
  }
}
