package com.metalr2.model.followArtist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Data
@IdClass(FollowedArtistEntity.class)
@Entity(name = "followed_artists")
@NoArgsConstructor
@AllArgsConstructor
public class FollowedArtistEntity implements Serializable {

  @Id
  @Column
  private long userId;

  @Id
  @Column
  private long artistDiscogsId;

}
