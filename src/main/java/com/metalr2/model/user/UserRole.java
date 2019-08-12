package com.metalr2.model.user;

import java.util.HashSet;
import java.util.Set;

public enum UserRole {

  USER,
  ADMINISTRATOR;

  public static Set<UserRole> createUserRole() {
    Set<UserRole> userRoleSet = new HashSet<>();
    userRoleSet.add(USER);

    return userRoleSet;
  }

  public static Set<UserRole> createSuperUserRole() {
    Set<UserRole> superUserRoleSet = createUserRole();
    superUserRoleSet.add(ADMINISTRATOR);

    return superUserRoleSet;
  }

}
