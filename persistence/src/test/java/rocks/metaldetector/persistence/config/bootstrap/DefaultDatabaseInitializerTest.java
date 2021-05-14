package rocks.metaldetector.persistence.config.bootstrap;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
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
class DefaultDatabaseInitializerTest implements WithAssertions {

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private DefaultDatabaseInitializer underTest;

  @Test
  @DisplayName("No demo data is generated if there are already users")
  void no_demo_data_is_generated() {
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
  @DisplayName("demo data is generated if there are no users.")
  void demo_data_is_generated() {
    // given
    TypedQuery typedQuery = Mockito.mock(TypedQuery.class);
    doReturn(typedQuery).when(entityManager).createQuery(anyString(), any());

    // given 1st query
    var emptyResultList = Collections.emptyList();
    doReturn(emptyResultList).when(typedQuery).getResultList();

    // given 2nd query
    UserEntity userMock = Mockito.mock(UserEntity.class);
    doReturn(typedQuery).when(typedQuery).setParameter(anyString(), anyString());
    doReturn(userMock).when(typedQuery).getSingleResult();

    // when
    underTest.run(null);

    // then
    verify(entityManager, times(4)).persist(any(UserEntity.class));

    // and
    verify(entityManager, times(3)).persist(any(ArtistEntity.class));
  }
}
