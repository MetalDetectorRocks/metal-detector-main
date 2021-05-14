package rocks.metaldetector.service.notification.messaging;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.NotificationChannel;

import java.util.function.Function;

@Component
public class NotificationSenderSupplier implements Function<NotificationChannel, NotificationSender> {

  private final NotificationSender emailService;
  private final NotificationSender telegramService;

  public NotificationSenderSupplier(@Qualifier("emailNotificationSender") NotificationSender emailService,
                                    @Qualifier("telegramNotificationSender") NotificationSender telegramService) {
    this.emailService = emailService;
    this.telegramService = telegramService;
  }

  @Override
  public NotificationSender apply(NotificationChannel channel) {
    switch (channel) {
      case EMAIL:
        return emailService;
      case TELEGRAM:
        return telegramService;
      default:
        throw new IllegalArgumentException("NotificationChannel '" + channel.name() + "' not supported");
    }
  }
}
