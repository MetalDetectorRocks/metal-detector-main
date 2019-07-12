package com.metalr2.model.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name="tokens")
public class TokenEntity implements Serializable {

  public static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private long id;

  @Column(nullable = false)
  private String tokenString;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private TokenType tokenType;

  @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, name = "users_id")
  private UserEntity user;

  @Column(nullable = false)
  private LocalDateTime expirationDateTime;

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expirationDateTime);
  }

}
