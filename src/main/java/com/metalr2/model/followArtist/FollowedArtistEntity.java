package com.metalr2.model.followArtist;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Getter
@IdClass(FollowedArtistEntity.class)
@Entity(name = "followed_artists")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FollowedArtistEntity implements Serializable {

  @Id
  @Column(name = "user_id", nullable = false, updatable = false)
  private long userId;

  @Id
  @Column(name = "artist_discogs_id", nullable = false, updatable = false)
  private long artistDiscogsId;

}
