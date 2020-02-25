package rocks.metaldetector.service;

import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for CRUD-Operations.
 * @param <T> DTO for Request
 * @param <U> DTO for response
 */
public interface SimpleCrudService<T, U> {

  @NonNull
  List<U> findAll();

  @NonNull
  List<U> findAll(int currentPage, int itemsPerPage);

  @NonNull
  Optional<U> find(long id);

  @NonNull
  U create(@NonNull T request);

  @NonNull
  List<U> createAll(@NonNull Iterable<T> request);

  @NonNull
  Optional<U> update(long id, @NonNull T request);

  boolean delete(long id);

}
