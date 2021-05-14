package rocks.metaldetector.persistence.config.bootstrap;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DatabaseInitializerTest implements WithAssertions {

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private DatabaseInitializer underTest;

  @Test
  @DisplayName("No Default Administrator account is created if users exist.")
  void no_default_administrator_account_is_created() {
    // given
    var resultList = List.of(
            UserFactory.createUser("demo", "demo@example.com")
    );
    TypedQuery typedQuery = Mockito.mock(TypedQuery.class);
    doReturn(resultList).when(typedQuery).getResultList();
    doReturn(typedQuery).when(entityManager).createQuery(anyString(), any());

    // when
    underTest.run(null);

    // then
    verifyNoMoreInteractions(entityManager);
  }

  @Test
  @DisplayName("A Default Administrator account is created if no users exist.")
  void default_administrator_account_is_created() {
    // given
    var resultList = Collections.emptyList();
    TypedQuery typedQuery = Mockito.mock(TypedQuery.class);
    doReturn(resultList).when(typedQuery).getResultList();
    doReturn(typedQuery).when(entityManager).createQuery(anyString(), any());

    // when
    underTest.run(null);

    // then
    verify(entityManager, times(1)).persist(any(UserEntity.class));
  }
}
