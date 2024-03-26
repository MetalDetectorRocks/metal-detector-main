package rocks.metaldetector.persistence.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rocks.metaldetector.persistence.domain.BaseEntity;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity(name = "oauth_authorization_states")
public class OAuthAuthorizationStateEntity extends BaseEntity {

  @OneToOne(targetEntity = UserEntity.class)
  @JoinColumn(nullable = false, name = "users_id")
  @Getter
  private AbstractUserEntity user;

  @Column(name = "state", nullable = false, unique = true)
  private String state;
}
