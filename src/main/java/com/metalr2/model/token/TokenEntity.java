package com.metalr2.model.token;

import com.metalr2.model.AbstractEntity;
import com.metalr2.model.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name="tokens")
public class TokenEntity extends AbstractEntity {

  @Column(name = "token_string", nullable = false)
  private String tokenString;

  @Column(name = "token_type", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private TokenType tokenType;

  @OneToOne(targetEntity = UserEntity.class)
  @JoinColumn(nullable = false, name = "users_id")
  private UserEntity user;

  @Column(name = "expiration_date_time", nullable = false)
  private LocalDateTime expirationDateTime;

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expirationDateTime);
  }

}
