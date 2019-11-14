package com.metalr2.model.followArtist;

import com.metalr2.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Getter
@Entity(name = "followed_artists")
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@AllArgsConstructor
public class FollowedArtistEntity extends BaseEntity implements Serializable {

  @Column(name = "public_user_id", nullable = false, updatable = false)
  @NonNull
  private String publicUserId;

  @Column(name = "artist_discogs_id", nullable = false, updatable = false)
  private long artistDiscogsId;

}
