package com.metalr2.service.followArtist;

import com.metalr2.model.followArtist.FollowedArtistEntity;
import com.metalr2.model.followArtist.FollowedArtistsRepository;
import com.metalr2.web.dto.FollowArtistDto;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class FollowDiscogsArtistServiceTest implements WithAssertions {

  private static final String userId        = "1";
  private static final String unknownUserId = "";
  private static final String artistName    = "Darkthrone";
  private static final long artistDiscogsId = 252211L;

  @Mock
  private FollowedArtistsRepository followedArtistsRepository;

  @InjectMocks
  private FollowArtistServiceImpl followArtistService;

  private FollowArtistDto followArtistDto;

  @BeforeEach
  void setUp() {
    this.followArtistDto = new FollowArtistDto(userId, artistName, artistDiscogsId);
  }

  @AfterEach
  void tearDown() {
    reset(followedArtistsRepository);
  }

  @Test
  @DisplayName("Following an artist for a given user id should work")
  void follow_artist(){
    // given
    ArgumentCaptor<FollowedArtistEntity> followArtistEntityCaptor = ArgumentCaptor.forClass(FollowedArtistEntity.class);

    when(followedArtistsRepository.save(any(FollowedArtistEntity.class))).thenReturn(new FollowedArtistEntity(userId, artistName, artistDiscogsId));

    // when
    followArtistService.followArtist(followArtistDto);

    // then
    verify(followedArtistsRepository, times(1)).save(followArtistEntityCaptor.capture());

    assertThat(followArtistEntityCaptor.getValue().getPublicUserId()).isEqualTo(userId);
    assertThat(followArtistEntityCaptor.getValue().getArtistDiscogsId()).isEqualTo(artistDiscogsId);
  }

  @Test
  @DisplayName("Unfollowing a combination of artist and user which exist should return true")
  void unfollow_existing_artist_should_return_true(){
    // given
    when(followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(Optional.of(new FollowedArtistEntity(userId, artistName, artistDiscogsId)));

    // when
    boolean result = followArtistService.unfollowArtist(followArtistDto);

    // then
    assertThat(result).isTrue();

    verify(followedArtistsRepository, times(1)).findByPublicUserIdAndArtistDiscogsId(userId, artistDiscogsId);
    verify(followedArtistsRepository, times(1)).delete(new FollowedArtistEntity(userId, artistName, artistDiscogsId));
  }

  @Test
  @DisplayName("Unfollowing a combination of artist and user which do not exist should return false")
  void unfollow_not_existing_artist_should_return_false(){
    // given
    FollowArtistDto followArtistDto = new FollowArtistDto(unknownUserId, artistName, artistDiscogsId);

    when(followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(Optional.empty());

    // when
    boolean result = followArtistService.unfollowArtist(followArtistDto);

    // then
    assertThat(result).isFalse();

    verify(followedArtistsRepository, times(1)).findByPublicUserIdAndArtistDiscogsId(unknownUserId, artistDiscogsId);
    verify(followedArtistsRepository, times(0)).delete(new FollowedArtistEntity(unknownUserId, artistName, artistDiscogsId));
  }

  @Test
  @DisplayName("exists() should return true if the given combination from user id and artist discogs id exists")
  void exists_should_return_true_for_existing_entity(){
    // given
    when(followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(true);

    // when
    boolean result = followArtistService.exists(followArtistDto);

    // then
    assertThat(result).isTrue();

    verify(followedArtistsRepository, times(1)).existsByPublicUserIdAndArtistDiscogsId(userId, artistDiscogsId);
  }

  @Test
  @DisplayName("exists() should return false if the given combination from user id and artist discogs id does not exist")
  void exists_should_return_false_for_not_existing_entity(){
    // given
    when(followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(false);

    // when
    boolean result = followArtistService.exists(followArtistDto);

    // then
    assertThat(result).isFalse();

    verify(followedArtistsRepository, times(1)).existsByPublicUserIdAndArtistDiscogsId(userId, artistDiscogsId);
  }

  @Test
  @DisplayName("findPerUser() finds the correct entities for a given user id if it exists")
  void find_per_user_finds_correct_entities(){
    // given
    when(followedArtistsRepository.findAllByPublicUserId(anyString())).thenReturn(Collections.singletonList(new FollowedArtistEntity(userId, artistName, artistDiscogsId)));

    // when
    List<FollowArtistDto> followArtistDtos = followArtistService.findPerUser(userId);

    // then
    assertThat(followArtistDtos).isNotEmpty();
    assertThat(followArtistDtos.get(0).getArtistDiscogsId()).isEqualTo(artistDiscogsId);
    assertThat(followArtistDtos.get(0).getPublicUserId()).isEqualTo(userId);

    verify(followedArtistsRepository, times(1)).findAllByPublicUserId(userId);
  }

  @Test
  @DisplayName("findPerUser() returns empty list if a given user id does not exists")
  void find_per_user_returns_empty_list(){
    // given
    when(followedArtistsRepository.findAllByPublicUserId(anyString())).thenReturn(Collections.EMPTY_LIST);

    // when
    List<FollowArtistDto> followArtistDtos = followArtistService.findPerUser(unknownUserId);

    // then
    assertThat(followArtistDtos).isEmpty();

    verify(followedArtistsRepository, times(1)).findAllByPublicUserId(unknownUserId);
  }
}
