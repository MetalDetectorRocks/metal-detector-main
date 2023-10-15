package rocks.metaldetector.service.imports;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import rocks.metaldetector.butler.facade.ButlerJobService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.telegram.TelegramService;
import rocks.metaldetector.service.user.UserEntityFactory;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.service.imports.JobCompletedEventListener.JOB_FAILED_MESSAGE;
import static rocks.metaldetector.service.imports.JobCompletedEventListener.JOB_RUNNING_MESSAGE;
import static rocks.metaldetector.service.imports.JobCompletedEventListener.PRINCIPAL;

@ExtendWith(MockitoExtension.class)
public class JobCompletedEventListenerTest implements WithAssertions {

  @Mock
  private ButlerJobService butlerJobService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TelegramService telegramService;

  @InjectMocks
  private JobCompletedEventListener underTest;

  @AfterEach
  void tearDown() {
    reset(butlerJobService, userRepository, telegramService);
  }

  @Test
  @DisplayName("current authentication is set to anonymous and then to null again")
  void test_current_authentication_set_to_anonymous() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    doReturn(ImportJobResultDto.builder().state("SUCCESSFUL").build()).when(butlerJobService).queryImportJob(any());

    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      //given
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      //when
      underTest.handle(new JobCompletedEvent("666"));

      //then
      mock.verify(SecurityContextHolder::getContext);
    }

    // then
    InOrder order = Mockito.inOrder(securityContextMock);
    order.verify(securityContextMock).setAuthentication(PRINCIPAL);
    order.verify(securityContextMock).setAuthentication(null);
  }

  @Test
  @DisplayName("butlerJobService is called")
  void test_butler_job_service_is_called() {
    // given
    var jobId = "666";
    doReturn(ImportJobResultDto.builder().state("Successful").build()).when(butlerJobService).queryImportJob(any());

    //when
    underTest.handle(new JobCompletedEvent(jobId));

    //then
    verify(butlerJobService).queryImportJob(jobId);
  }

  @Test
  @DisplayName("state Successful: userRepository is not called to find admins")
  void test_successful_user_repository_is_not_called() {
    // given
    doReturn(ImportJobResultDto.builder().state("Successful").build()).when(butlerJobService).queryImportJob(any());

    //when
    underTest.handle(new JobCompletedEvent("666"));

    //then
    verifyNoInteractions(userRepository);
  }

  @Test
  @DisplayName("state Error: userRepository is called to find admins")
  void test_error_user_repository_is_called() {
    // given
    doReturn(ImportJobResultDto.builder().state("Error").build()).when(butlerJobService).queryImportJob(any());

    //when
    underTest.handle(new JobCompletedEvent("666"));

    //then
    verify(userRepository).findByUserRolesContaining(ROLE_ADMINISTRATOR);
  }

  @Test
  @DisplayName("state Running: userRepository is called to find admins")
  void test_running_user_repository_is_called() {
    // given
    doReturn(ImportJobResultDto.builder().state("Running").build()).when(butlerJobService).queryImportJob(any());

    //when
    underTest.handle(new JobCompletedEvent("666"));

    //then
    verify(userRepository).findByUserRolesContaining(ROLE_ADMINISTRATOR);
  }

  @Test
  @DisplayName("state Error: telegramService is called with user")
  void test_error_telegram_messaging_service_is_called_with_user() {
    // given
    var importDto = ImportJobResultDto.builder().state("Error").source("Source1").startTime(LocalDateTime.now()).build();
    var user = UserEntityFactory.createDefaultUser();
    doReturn(importDto).when(butlerJobService).queryImportJob(any());
    doReturn(List.of(user)).when(userRepository).findByUserRolesContaining(any());

    //when
    underTest.handle(new JobCompletedEvent("666"));

    //then
    verify(telegramService).sendMessage(eq(user), anyString());
  }

  @Test
  @DisplayName("state Running: telegramService is called with user")
  void test_running_telegram_messaging_service_is_called_with_user() {
    // given
    var importDto = ImportJobResultDto.builder().state("Running").source("Source1").startTime(LocalDateTime.now()).build();
    var user = UserEntityFactory.createDefaultUser();
    doReturn(importDto).when(butlerJobService).queryImportJob(any());
    doReturn(List.of(user)).when(userRepository).findByUserRolesContaining(any());

    //when
    underTest.handle(new JobCompletedEvent("666"));

    //then
    verify(telegramService).sendMessage(eq(user), anyString());
  }

  @Test
  @DisplayName("state Error: telegramService is called with message")
  void test_error_telegram_messaging_service_is_called_with_message() {
    // given
    var importDto = ImportJobResultDto.builder().state("Error").source("Source1").startTime(LocalDateTime.now()).build();
    var user = UserEntityFactory.createDefaultUser();
    var expectedMessage = JOB_FAILED_MESSAGE.replace("%1", importDto.getSource())
        .replace("%2", importDto.getStartTime().format(ISO_LOCAL_DATE_TIME));
    doReturn(importDto).when(butlerJobService).queryImportJob(any());
    doReturn(List.of(user)).when(userRepository).findByUserRolesContaining(any());

    //when
    underTest.handle(new JobCompletedEvent("666"));

    //then
    verify(telegramService).sendMessage(any(), eq(expectedMessage));
  }

  @Test
  @DisplayName("state Running: telegramService is called with message")
  void test_running_telegram_messaging_service_is_called_with_message() {
    // given
    var importDto = ImportJobResultDto.builder().state("Running").source("Source1").startTime(LocalDateTime.now()).build();
    var user = UserEntityFactory.createDefaultUser();
    var expectedMessage = JOB_RUNNING_MESSAGE.replace("%1", importDto.getSource())
        .replace("%2", importDto.getStartTime().format(ISO_LOCAL_DATE_TIME));
    doReturn(importDto).when(butlerJobService).queryImportJob(any());
    doReturn(List.of(user)).when(userRepository).findByUserRolesContaining(any());

    //when
    underTest.handle(new JobCompletedEvent("666"));

    //then
    verify(telegramService).sendMessage(any(), eq(expectedMessage));
  }
}
