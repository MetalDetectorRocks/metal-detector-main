package rocks.metaldetector.persistence.domain.notification;

public enum NotificationChannel {

  EMAIL,
  TELEGRAM;

  public static NotificationChannel getChannelFromString(String channel) {
    if (channel.equalsIgnoreCase(EMAIL.name())) {
      return EMAIL;
    }
    if (channel.equalsIgnoreCase(TELEGRAM.name())) {
      return TELEGRAM;
    }
    throw new IllegalArgumentException("Channel '" + channel + "' not found");
  }
}
