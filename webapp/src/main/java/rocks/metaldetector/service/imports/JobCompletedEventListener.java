package rocks.metaldetector.service.imports;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.facade.ButlerJobService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;

@Slf4j
@Component
@AllArgsConstructor
public class JobCompletedEventListener {

  protected static final String JOB_FAILED_MESSAGE = "Import job for source '%1' from '%2' failed.";
  protected static final String JOB_RUNNING_MESSAGE = "Import job for source '%1' from '%2' is still running.";
  protected static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "jobCompletedListenerPrincipal", createAuthorityList("ROLE_ADMINISTRATOR"));

  private final ButlerJobService butlerJobService;
  private final TelegramMessagingService telegramMessagingService;
  private final TelegramConfigRepository telegramConfigRepository;
  private final UserRepository userRepository;

  @EventListener
  public void handle(JobCompletedEvent event) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    try {
      securityContext.setAuthentication(PRINCIPAL);
      ImportJobResultDto importJob = butlerJobService.queryImportJob(event.jobId());

      if (importJob.getState().equalsIgnoreCase("Error")) {
        sendJobStateMessage(JOB_FAILED_MESSAGE, importJob);
      }
      else if (importJob.getState().equalsIgnoreCase("Running")) {
        sendJobStateMessage(JOB_RUNNING_MESSAGE, importJob);
      }
    }
    finally {
      securityContext.setAuthentication(null);
    }
  }

  private void sendJobStateMessage(String message, ImportJobResultDto importJob) {
    List<AbstractUserEntity> admins = userRepository.findByUserRolesContaining(ROLE_ADMINISTRATOR);

    for (AbstractUserEntity admin : admins) {
      telegramConfigRepository
          .findByUser(admin)
          .ifPresent(telegramConfig -> {
            var chatId = telegramConfig.getChatId();
            if (chatId != null) {
              var formattedMessage = message
                  .replace("%1", importJob.getSource())
                  .replace("%2", importJob.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
              telegramMessagingService.sendMessage(chatId, formattedMessage);
            }
          });
    }
  }
}
