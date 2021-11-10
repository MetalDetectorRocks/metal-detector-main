package rocks.metaldetector.service.cleanup;

public interface RegistrationCleanupService {

  void cleanupUsersWithExpiredToken();
}
