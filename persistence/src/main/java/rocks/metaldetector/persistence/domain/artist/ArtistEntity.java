package rocks.metaldetector.persistence.domain.artist;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import rocks.metaldetector.persistence.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity(name = "artists")
public class ArtistEntity extends BaseEntity implements MultipleSizeImages {

  @Column(name = "external_id", nullable = false, updatable = false)
  private String externalId;

  @Column(name = "external_url")
  private String externalUrl;

  @Column(name = "artist_name", nullable = false)
  @NonNull
  private String artistName;

  @Column(name = "genres")
  private String genres;

  @Column(name = "source", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private ArtistSource source;

  @Column(name = "spotify_popularity")
  private Integer spotifyPopularity;

  @Column(name = "spotify_follower")
  private Integer spotifyFollower;

  @Column(name = "image_xs")
  private String imageXs;

  @Column(name = "image_s")
  private String imageS;

  @Column(name = "image_m")
  private String imageM;

  @Column(name = "image_l")
  private String imageL;

  public List<String> getGenresAsList() {
    return genres == null || genres.isBlank() ?
            Collections.emptyList() :
            Stream.of(genres.split(",")).map(String::trim).collect(Collectors.toList());
  }
}
