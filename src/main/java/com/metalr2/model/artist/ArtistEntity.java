package com.metalr2.model.artist;

import com.metalr2.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Entity(name = "artists")
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@AllArgsConstructor
@EqualsAndHashCode
public class ArtistEntity extends BaseEntity {

  @Column(name = "artist_discogs_id", nullable = false, updatable = false)
  @NonNull
  private long artistDiscogsId;

  @Column(name = "artist_name", nullable = false, updatable = false)
  @NonNull
  private String artistName;

  @Column(name = "thumb", updatable = false)
  private String thumb;

}
