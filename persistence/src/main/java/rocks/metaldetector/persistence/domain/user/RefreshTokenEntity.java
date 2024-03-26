package rocks.metaldetector.persistence.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rocks.metaldetector.persistence.domain.BaseEntity;

@Entity(name = "refresh_tokens")
@NoArgsConstructor
@Setter
public class RefreshTokenEntity extends BaseEntity {

  @OneToOne(targetEntity = UserEntity.class)
  @JoinColumn(nullable = false, name = "users_id")
  @Getter
  private AbstractUserEntity user;

  @Column(name = "token", unique = true)
  private String token;
}
