package rocks.metaldetector.persistence.domain.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for lombok builder
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity(name = "notificationConfigs")
public class NotificationConfigEntity extends BaseEntity {

  @OneToOne(targetEntity = UserEntity.class)
  @JoinColumn(nullable = false, name = "users_id")
  private UserEntity user;

  @Column(name = "notify", nullable = false, columnDefinition = "boolean default false")
  @Setter
  @Builder.Default
  private Boolean notify = false;

  @Column(name = "frequencyInWeeks", nullable = false, columnDefinition = "integer default 4")
  @Setter
  @Builder.Default
  private Integer frequencyInWeeks = 4;

  @Column(name = "notificationAtReleaseDate", nullable = false, columnDefinition = "boolean default false")
  @Setter
  @Builder.Default
  private Boolean notificationAtReleaseDate = false;

  @Column(name = "notificationAtAnnouncementDate", nullable = false, columnDefinition = "boolean default false")
  @Setter
  @Builder.Default
  private Boolean notificationAtAnnouncementDate = false;

}
