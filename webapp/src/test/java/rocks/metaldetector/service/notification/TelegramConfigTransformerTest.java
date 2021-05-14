package rocks.metaldetector.service.notification;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;

class TelegramConfigTransformerTest implements WithAssertions {

  private final TelegramConfigTransformer underTest = new TelegramConfigTransformer();

  @Test
  @DisplayName("entity is transformed to dto")
  void test_transform() {
    // given
    var entity = TelegramConfigEntity.builder()
        .registrationId(666)
        .chatId(555)
        .build();
    var expectedDto = TelegramConfigDto.builder()
        .registrationId(666)
        .chatId(555)
        .build();

    // when
    var result = underTest.transform(entity);

    // then
    assertThat(result).isEqualTo(expectedDto);
  }
}
