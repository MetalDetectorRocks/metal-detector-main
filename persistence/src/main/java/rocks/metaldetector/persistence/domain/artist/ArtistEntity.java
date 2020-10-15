package rocks.metaldetector.persistence.domain.artist;

import lombok.AccessLevel;
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

@Getter
@Entity(name = "artists")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@ToString
@EqualsAndHashCode(callSuper = true)
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

  @Builder
  public ArtistEntity(String externalId, String artistName, String thumb, ArtistSource source) {
    this.externalId = externalId;
    this.artistName = artistName;
    this.thumb = thumb;
    this.source = source;
  }
}
