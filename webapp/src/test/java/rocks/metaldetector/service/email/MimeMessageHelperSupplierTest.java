package rocks.metaldetector.service.email;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.mail.internet.MimeMessage;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.mock;

class MimeMessageHelperSupplierTest implements WithAssertions {

  private final MimeMessageHelperSupplier underTest = new MimeMessageHelperSupplier();

  @Test
  @DisplayName("should create mime message helper")
  void should_create_mime_message_helper() {
    // given
    MimeMessage mimeMessageMock = mock(MimeMessage.class);

    // when
    var mimeMessageHelper = underTest.apply(mimeMessageMock);

    // then
    assertThat(mimeMessageHelper.getMimeMessage()).isEqualTo(mimeMessageMock);
    assertThat(mimeMessageHelper.getMimeMultipart().getContentType()).startsWith("multipart/related");
    assertThat(mimeMessageHelper.getEncoding()).isEqualTo(UTF_8.name());
  }
}