package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.NotificationChannel;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class NotificationSenderFunction implements Function<NotificationChannel, NotificationSender> {

  private final NotificationSender emailNotificationSender;
  private final NotificationSender telegramNotificationSender;

  @Override
  public NotificationSender apply(NotificationChannel channel) {
    return switch (channel) {
      case EMAIL -> emailNotificationSender;
      case TELEGRAM -> telegramNotificationSender;
    };
  }
}
