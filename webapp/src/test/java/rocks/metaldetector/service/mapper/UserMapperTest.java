package rocks.metaldetector.service.mapper;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserMapper;

import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

import static rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;

class UserMapperTest implements WithAssertions {

  private static final String USERNAME = "JohnD";
  private static final String EMAIL = "john.doe@example.com";

  private UserMapper underTest = new UserMapper();

  @Test
  @DisplayName("Should map a UserEntity to UserDto")
  void map_entity_to_dto() {
    // given
    UserEntity entity = createUserEntity();
    entity.setPublicId("dummy-public-id");
    entity.setCreatedBy("Creator");
    entity.setCreatedDateTime(new Date());
    entity.setLastModifiedBy("Modifier");
    entity.setLastModifiedDateTime(new Date());
    UserDto expected = UserDto.builder()
            .publicId(entity.getPublicId())
            .username(USERNAME)
            .email(EMAIL)
            .plainPassword(null) // is only mapped from dto to entity
            .role("User")
            .enabled(true)
            .lastLogin(null) // feature is currently not available
            .createdBy(entity.getCreatedBy())
            .createdDateTime(entity.getCreatedDateTime())
            .lastModifiedBy(entity.getLastModifiedBy())
            .lastModifiedDateTime(entity.getLastModifiedDateTime())
            .build();

    // when
    UserDto result = underTest.mapToDto(entity);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest(name = "[{index}]: {0} => {1}")
  @MethodSource("userRoleProvider")
  @DisplayName("Should map the role of an UserEntity correctly")
  void map_role_to_dto(Set<UserRole> userRoles, String expectedDtoRole) {
    // given
    UserEntity entity = createUserEntity();
    entity.setUserRoles(userRoles);

    // when
    UserDto result = underTest.mapToDto(entity);

    // then
    assertThat(result.getRole()).isEqualTo(expectedDtoRole);
  }

  @Test
  @DisplayName("Should not map a UserDto to UserEntity")
  void map_dto_to_entity() {
    // given
    UserDto dto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);

    // when
    Throwable throwable =  catchThrowable(() -> underTest.mapToEntity(dto));

    // then
    assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
  }

  private static Stream<Arguments> userRoleProvider() {
    return Stream.of(
        Arguments.of(UserRole.createUserRole(), "User"),
        Arguments.of(UserRole.createAdministratorRole(), "Administrator")
    );
  }

  private static UserEntity createUserEntity() {
    return UserEntity.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(UserRole.createUserRole())
            .enabled(true)
            .build();
  }
}
