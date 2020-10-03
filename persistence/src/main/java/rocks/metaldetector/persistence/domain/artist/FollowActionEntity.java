package rocks.metaldetector.persistence.domain.artist;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Entity(name = "followActions")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@ToString
public class FollowActionEntity extends BaseEntity {

  @OneToOne(targetEntity = UserEntity.class)
  @JoinColumn(nullable = false, name = "user_id")
  @NonNull
  private UserEntity user;

  @OneToOne(targetEntity = ArtistEntity.class)
  @JoinColumn(nullable = false, name = "artist_id")
  @NonNull
  private ArtistEntity artist;

  @Builder
  public FollowActionEntity(@NonNull UserEntity user, @NonNull ArtistEntity artist) {
    this.user = user;
    this.artist = artist;
  }
}
