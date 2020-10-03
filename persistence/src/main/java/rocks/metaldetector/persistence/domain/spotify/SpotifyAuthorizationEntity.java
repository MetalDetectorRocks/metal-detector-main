package rocks.metaldetector.persistence.domain.spotify;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@Entity(name = "spotifyAuthorizations")
public class SpotifyAuthorizationEntity extends BaseEntity {

  @Column(name = "state", nullable = false, updatable = false)
  private String state;

  @Column(name = "access_token")
  private String accessToken;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "scope")
  private String scope;

  @Column(name = "token_type")
  private String tokenType;

  @Column(name = "expires_in")
  private Integer expiresIn;

  @OneToOne(mappedBy = "spotifyAuthorization")
  private UserEntity user;

  public SpotifyAuthorizationEntity(String state) {
    this.state = state;
  }
}
