package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.NotificationChannel;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class NotificationSenderSupplier implements Function<NotificationChannel, NotificationSender> {

  private final NotificationSender emailNotificationSender;
  private final NotificationSender telegramNotificationSender;

  @Override
  public NotificationSender apply(NotificationChannel channel) {
    switch (channel) {
      case EMAIL:
        return emailNotificationSender;
      case TELEGRAM:
        return telegramNotificationSender;
      default:
        throw new IllegalArgumentException("NotificationChannel '" + channel.name() + "' not supported");
    }
  }
}
