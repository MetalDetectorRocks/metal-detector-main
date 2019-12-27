package com.metalr2.config.constants;

import java.util.List;
import java.util.stream.Stream;

public class Endpoints {

  public static final String ERROR = "/error";

  public static class Guest {
    // General
    public static final String        INDEX                  = "/index";
    public static final String        EMPTY_INDEX            = "";
    public static final String        SLASH_INDEX            = "/";
    public static final String        LOGOUT                 = "/logout";
    public static final List<String>  ALL_GUEST_INDEX_PAGES  = List.of(INDEX, EMPTY_INDEX, SLASH_INDEX);

    // Authentication
    public static final String       LOGIN                     = "/login";
    public static final String       REGISTER                  = "/register";
    public static final String       REGISTRATION_VERIFICATION = "/registration-verification";
    public static final String       RESEND_VERIFICATION_TOKEN = "/resend-verification-token";
    public static final String       FORGOT_PASSWORD           = "/forgot-password";
    public static final String       RESET_PASSWORD            = "/reset-password";
    public static final List<String> ALL_AUTH_PAGES            = List.of(LOGIN, REGISTER, REGISTRATION_VERIFICATION,
                                                                         RESEND_VERIFICATION_TOKEN, FORGOT_PASSWORD, RESET_PASSWORD);
  }

  public static class Frontend {
    public static final String HOME                  = "/search-artists";
    public static final String SEARCH_ARTISTS        = "/search-artists";
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
    public static final String STATUS                = "/status";
  }

  public static class Rest {
    public static final String ARTISTS_V1 = "/rest/v1/artists";
    public static final String SEARCH     = "/search";
    public static final String FOLLOW     = "/follow";
    public static final String UNFOLLOW   = "/unfollow";
  }

  public static class AdminArea {
    public static final String INDEX        = "/admin";
    public static final String IMPORT       = "/admin/import";
    public static final String ANALYTICS    = "/admin/analytics";
    public static final String SETTINGS     = "/admin/settings";
    public static final String USERS_LIST   = "/admin/users";
  }

  public static class AntPattern {
    public static final String   ADMIN                     = "/admin/**";
           static final String[] INDEX                     = {"/", "/index", "/index/"};
    public static final String[] LOGIN                     = {"/login", "/login/"};
           static final String[] REGISTER                  = {"/register", "/register/"};
           static final String[] REGISTRATION_VERIFICATION = {"/registration-verification", "/registration-verification/"};
           static final String[] RESEND_VERIFICATION_TOKEN = {"/resend-verification-token", "/resend-verification-token/"};
           static final String[] FORGOT_PASSWORD           = {"/forgot-password", "/forgot-password/"};
           static final String[] RESET_PASSWORD            = {"/reset-password", "/reset-password/"};
    public static final String[] RESOURCES                 = {"/resources/**", "/css/**", "/js/**", "/images/**", "/webjars/**"};
    public static final String[] REST_ENDPOINTS            = {"/rest/**"};

    public static final String[] AUTH_PAGES = Stream.of(INDEX, LOGIN, REGISTER, REGISTRATION_VERIFICATION,
            RESEND_VERIFICATION_TOKEN, FORGOT_PASSWORD, RESET_PASSWORD).flatMap(Stream::of).toArray(String[]::new);
  }

}
