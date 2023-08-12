package rocks.metaldetector.persistence.domain.artist;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

@Getter
@Entity(name = "followActions")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for lombok builder
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class FollowActionEntity extends BaseEntity {

  @ManyToOne(targetEntity = AbstractUserEntity.class)
  @JoinColumn(nullable = false, name = "user_id")
  @NonNull
  private AbstractUserEntity user;

  @ManyToOne(targetEntity = ArtistEntity.class)
  @JoinColumn(nullable = false, name = "artist_id")
  @NonNull
  private ArtistEntity artist;

}
