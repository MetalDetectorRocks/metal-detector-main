package rocks.metaldetector.service.cleanup;

public interface CleanupService {

  void cleanupUsersWithExpiredToken();
}
