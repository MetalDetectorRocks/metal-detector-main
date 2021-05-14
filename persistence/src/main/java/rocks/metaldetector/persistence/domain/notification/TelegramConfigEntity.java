package rocks.metaldetector.persistence.domain.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rocks.metaldetector.persistence.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for lombok builder
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity(name = "telegramConfigs")
public class TelegramConfigEntity extends BaseEntity {

  @OneToOne(targetEntity = NotificationConfigEntity.class)
  @JoinColumn(nullable = false, name = "notification_configs_id")
  private NotificationConfigEntity notificationConfig;

  @Column(name = "chat_id")
  @Setter
  private Integer chatId;

  @Column(name = "registration_id")
  @Setter
  private Integer registrationId;

}
