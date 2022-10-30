package rocks.metaldetector.persistence.domain.user;

import lombok.NoArgsConstructor;
import lombok.Setter;
import rocks.metaldetector.persistence.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "refresh_tokens")
@NoArgsConstructor
@Setter
public class RefreshTokenEntity extends BaseEntity {

  @OneToOne(targetEntity = UserEntity.class)
  @JoinColumn(nullable = false, name = "users_id")
  private UserEntity user;

  @Column(name = "token", unique = true)
  private String token;
}
