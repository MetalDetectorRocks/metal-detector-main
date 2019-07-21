package com.metalr2.config.constants;

public class Endpoints {

  // General
  public static final String INDEX          = "/index";
  public static final String EMPTY_INDEX    = "";
  public static final String SLASH_INDEX    = "/";

  // Search
  public static final String SEARCH         ="/search";
  public static final String SEARCH_RESULT  ="/searchResult";


  // Users
  public static final String USERS_LIST     = "/admin/users";
  public static final String USERS_CREATE   = "/admin/users/create";
  public static final String USERS_EDIT     = "/admin/users/edit/{id}";

  // Errors
  public static final String ERROR          = "/error";

  // Authentication
  public static final String LOGIN                     = "/login";
  public static final String REGISTER                  = "/register";
  public static final String REGISTRATION_VERIFICATION = "/registration-verification";
  public static final String RESEND_VERIFICATION_TOKEN = "/resend-verification-token";
  public static final String FORGOT_PASSWORD           = "/forgot-password";
  public static final String RESET_PASSWORD            = "/reset-password";
  public static final String LOGOUT                    = "/logout";

  public static class AntPattern {
    public static final String   ADMIN                     = "/backend/**";
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
