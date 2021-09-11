package rocks.metaldetector.persistence.domain.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for lombok builder
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity(name = "notificationConfigs")
public class NotificationConfigEntity extends BaseEntity {

  @OneToOne(targetEntity = AbstractUserEntity.class)
  @JoinColumn(nullable = false, name = "users_id")
  private AbstractUserEntity user;

  @Column(name = "channel", nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationChannel channel;

  @Column(name = "frequency_in_weeks", nullable = false, columnDefinition = "integer default 4")
  @Setter
  @Builder.Default
  private Integer frequencyInWeeks = 4;

  @Column(name = "notification_at_release_date", nullable = false, columnDefinition = "boolean default true")
  @Setter
  @Builder.Default
  private Boolean notificationAtReleaseDate = true;

  @Column(name = "notification_at_announcement_date", nullable = false, columnDefinition = "boolean default true")
  @Setter
  @Builder.Default
  private Boolean notificationAtAnnouncementDate = true;

  @Column(name = "notify_reissues", nullable = false, columnDefinition = "boolean default false")
  @Setter
  @Builder.Default
  private Boolean notifyReissues = false;

  @Column(name = "last_notification_date")
  @Setter
  private LocalDate lastNotificationDate;

}
