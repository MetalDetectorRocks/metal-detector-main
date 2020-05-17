package rocks.metaldetector.persistence.domain.artist;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity(name = "artists")
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@ToString
@EqualsAndHashCode(callSuper = true, exclude = "followedByUsers")
public class ArtistEntity extends BaseEntity {

  @Column(name = "artist_discogs_id", nullable = false, updatable = false)
  private long artistDiscogsId;

  @Column(name = "artist_name", nullable = false, updatable = false)
  @NonNull
  private String artistName;

  @Column(name = "thumb", updatable = false)
  private String thumb;

  @ManyToMany(mappedBy = "followedArtists")
  private Set<UserEntity> followedByUsers;

  public ArtistEntity(long artistDiscogsId, String artistName, String thumb) {
    this.artistDiscogsId = artistDiscogsId;
    this.artistName = artistName;
    this.thumb = thumb;
    this.followedByUsers = new HashSet<>();
  }

  public Set<UserEntity> getFollowedByUsers() {
    return Set.copyOf(followedByUsers);
  }

  public void addFollowing(UserEntity userEntity) {
    followedByUsers.add(userEntity);
  }

  public void removeFollowing(UserEntity userEntity) {
    followedByUsers.remove(userEntity);
  }
}
