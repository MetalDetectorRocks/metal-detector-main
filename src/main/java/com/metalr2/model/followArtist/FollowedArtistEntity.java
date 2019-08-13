package com.metalr2.model.followArtist;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Data
@IdClass(FollowedArtistEntity.class)
@Entity(name = "followed_artists")
public class FollowedArtistEntity implements Serializable {

  public static final long serialVersionUID = 1L;

  @Id
  @Column
  private final long userId;

  @Id
  @Column
  private final long artistDiscogsId;

}
