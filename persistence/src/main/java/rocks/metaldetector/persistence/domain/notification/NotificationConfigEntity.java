package rocks.metaldetector.persistence.domain.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for lombok builder
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity(name = "notificationConfigs")
public class NotificationConfigEntity extends BaseEntity {

  @OneToOne(mappedBy = "notificationConfig")
  private UserEntity user;

  @Column(name = "frequency", columnDefinition = "integer default 4")
  @NonNull
  private Integer frequency = 4;

  @Column(name = "notificationAtReleaseDate", columnDefinition = "boolean default false")
  @NonNull
  private Boolean notificationAtReleaseDate = false;

  @Column(name = "notificationAtAnnouncementDate", columnDefinition = "boolean default false")
  @NonNull
  private Boolean notificationAtAnnouncementDate = false;
}
