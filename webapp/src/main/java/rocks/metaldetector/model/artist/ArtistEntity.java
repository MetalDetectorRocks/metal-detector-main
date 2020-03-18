package rocks.metaldetector.model.artist;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import rocks.metaldetector.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Entity(name = "artists")
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class ArtistEntity extends BaseEntity {

  @Column(name = "artist_discogs_id", nullable = false, updatable = false)
  private long artistDiscogsId;

  @Column(name = "artist_name", nullable = false, updatable = false)
  @NonNull
  private String artistName;

  @Column(name = "thumb", updatable = false)
  private String thumb;

}
