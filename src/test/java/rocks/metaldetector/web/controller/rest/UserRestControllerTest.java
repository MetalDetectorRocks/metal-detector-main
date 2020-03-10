package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MimeTypeUtils;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.DtoFactory;
import rocks.metaldetector.web.dto.UserDto;
import rocks.metaldetector.web.dto.response.UserResponse;

import java.util.List;

import static io.restassured.module.mockmvc.config.MockMvcConfig.mockMvcConfig;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserRestControllerTest implements WithAssertions {

  private static final String REQUEST_URI = "http://localhost" + Endpoints.Rest.USERS;

  @Mock
  private UserService userService;

  @Spy
  ModelMapper modelMapper;

  @InjectMocks
  private UserRestController underTest;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    underTest = new UserRestController(userService, modelMapper);
    MockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(underTest);
    RestAssuredMockMvc.standaloneSetup(mockMvcBuilder,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
    mockMvc = mockMvcBuilder.build();
  }

  @AfterEach
  void tearDown() {
    reset(userService);
  }

  @Test
  @DisplayName("Should return all users")
  void should_return_all_users_mockMvc() throws Exception {
    // given
    UserDto dto1 = DtoFactory.UserDtoFactory.withUsernameAndEmail("user1", "user1@example.com");
    UserDto dto2 = DtoFactory.UserDtoFactory.withUsernameAndEmail("user2", "user2@example.com");
    UserDto dto3 = DtoFactory.UserDtoFactory.withUsernameAndEmail("user3", "user3@example.com");
    when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2, dto3));

    ResultActions result = mockMvc.perform(get(REQUEST_URI).accept(MimeTypeUtils.APPLICATION_JSON_VALUE));

    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.length()").value(3));
//    result.andExpect(jsonPath("$", hasItems(List.of(dto1, dto2, dto3))));
  }

  @Test
  @DisplayName("Should return all users")
  void should_return_all_users_rest_assured() {
    // given
    UserDto dto1 = DtoFactory.UserDtoFactory.withUsernameAndEmail("user1", "user1@example.com");
    UserDto dto2 = DtoFactory.UserDtoFactory.withUsernameAndEmail("user2", "user2@example.com");
    UserDto dto3 = DtoFactory.UserDtoFactory.withUsernameAndEmail("user3", "user3@example.com");
    when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2, dto3));

    MockMvcResponse response = RestAssuredMockMvc.given()
        .accept(ContentType.JSON)
        .when()
        .get(REQUEST_URI);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

    List<UserResponse> userList = response.jsonPath().getList(".", UserResponse.class);
    assertThat(userList).hasSize(3);
    assertThat(userList.get(0)).isEqualTo(modelMapper.map(dto1, UserResponse.class));
    assertThat(userList.get(1)).isEqualTo(modelMapper.map(dto2, UserResponse.class));
    assertThat(userList.get(2)).isEqualTo(modelMapper.map(dto3, UserResponse.class));
  }
}
