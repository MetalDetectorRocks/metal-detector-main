package com.metalr2.model.user;

import java.util.HashSet;
import java.util.Set;

/**
 * The enum names must be prefixed with ROLE_.
 */
public enum UserRole {

  ROLE_USER {
    @Override
    public String getName() {
      return "USER";
    }
  },
  ROLE_ADMINISTRATOR {
    @Override
    public String getName() {
      return "ADMINISTRATOR";
    }
  };

  public abstract String getName();

  public static Set<UserRole> createUserRole() {
    Set<UserRole> userRoleSet = new HashSet<>();
    userRoleSet.add(ROLE_USER);

    return userRoleSet;
  }

  public static Set<UserRole> createAdministratorRole() {
    Set<UserRole> userRoleSet = new HashSet<>();
    userRoleSet.add(ROLE_ADMINISTRATOR);

    return userRoleSet;
  }
}
