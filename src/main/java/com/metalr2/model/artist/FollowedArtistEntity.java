package com.metalr2.model.artist;

import com.metalr2.model.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

  @Column(name = "discogs_id", nullable = false, updatable = false)
  private long discogsId;

}
