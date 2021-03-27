package rocks.metaldetector.persistence.domain.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for lombok builder
@Entity(name="tokens")
public class TokenEntity extends BaseEntity {

  @Column(name = "token_string", nullable = false)
  @Lob
  @Type(type = "org.hibernate.type.TextType")
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
