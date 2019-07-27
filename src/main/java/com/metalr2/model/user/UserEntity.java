package com.metalr2.model.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity(name="users")
public class UserEntity implements Serializable {

  public static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private long id;

  @Column(nullable=false)
  private final String userId;

  @Column(nullable=false, length=50, unique = true)
  private String userName;

  @Column(nullable=false, length=120, unique = true)
  private String email;

  @Column(nullable=false, length = 60)
  private String encryptedPassword;

  @Column
  private boolean enabled;

  public UserEntity() {
    this.userId = UUID.randomUUID().toString();
  }

}
