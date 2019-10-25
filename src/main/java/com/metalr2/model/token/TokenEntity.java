package com.metalr2.model.token;

import com.metalr2.model.BaseEntity;
import com.metalr2.model.user.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for lombok builder
@Entity(name="tokens")
public class TokenEntity extends BaseEntity {

  @Column(name = "token_string", nullable = false)
  @Lob
  @NonNull
  private String tokenString;

  @Column(name = "token_type", nullable = false)
  @NonNull
  @Enumerated(value = EnumType.STRING)
  private TokenType tokenType;

  @OneToOne(targetEntity = UserEntity.class)
  @JoinColumn(nullable = false, name = "users_id")
  @NonNull
  private UserEntity user;

  @Column(name = "expiration_date_time", nullable = false)
  @NonNull
  private LocalDateTime expirationDateTime;

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expirationDateTime);
  }

}
