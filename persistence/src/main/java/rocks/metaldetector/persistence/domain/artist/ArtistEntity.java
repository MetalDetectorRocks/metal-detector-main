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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity(name = "artists")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@ToString
@EqualsAndHashCode(callSuper = true, exclude = "followedByUsers")
public class ArtistEntity extends BaseEntity {

  @Column(name = "external_id", nullable = false, updatable = false)
  private String externalId;

  @Column(name = "artist_name", nullable = false, updatable = false)
  @NonNull
  private String artistName;

  @Column(name = "thumb", updatable = false)
  private String thumb;

  @Column(name = "source", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private ArtistSource source;

  @ManyToMany(mappedBy = "followedArtists")
  private Set<UserEntity> followedByUsers;

  public ArtistEntity(String externalId, String artistName, String thumb, ArtistSource source) {
    this.externalId = externalId;
    this.artistName = artistName;
    this.thumb = thumb;
    this.source = source;
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
