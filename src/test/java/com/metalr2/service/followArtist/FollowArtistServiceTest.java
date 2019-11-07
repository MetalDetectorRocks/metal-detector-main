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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowArtistServiceTest implements WithAssertions {

  private static final long userId           = 1L;
  private static final long artistDiscogsId = 252211L;

  @Mock
  private FollowedArtistsRepository followedArtistsRepository;

  @InjectMocks
  private FollowArtistServiceImpl followArtistService;

  @BeforeEach
  void setUp() {
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
    FollowArtistDto followArtistDto = new FollowArtistDto(userId, artistDiscogsId);

    when(followedArtistsRepository.save(any(FollowedArtistEntity.class))).thenReturn(new FollowedArtistEntity(userId, artistDiscogsId));

    // when
    FollowArtistDto savedFollowArtistDto = followArtistService.followArtist(followArtistDto);

    verify(followedArtistsRepository, times(1)).save(followArtistEntityCaptor.capture());

    // then
    assertThat(savedFollowArtistDto).isNotNull();
    assertThat(savedFollowArtistDto.getUserId()).isEqualTo(userId);
    assertThat(savedFollowArtistDto.getArtistDiscogsId()).isEqualTo(artistDiscogsId);

    assertThat(followArtistEntityCaptor.getValue().getUserId()).isEqualTo(userId);
    assertThat(followArtistEntityCaptor.getValue().getArtistDiscogsId()).isEqualTo(artistDiscogsId);
  }

  @Test
  @DisplayName("Unfollowing an artist for a given user id should work")
  void unfollow_artist(){
    // given
    FollowArtistDto followArtistDto = new FollowArtistDto(userId, artistDiscogsId);

    // when
    followArtistService.unfollowArtist(followArtistDto);

    // then
    verify(followedArtistsRepository, times(1)).delete(new FollowedArtistEntity(userId, artistDiscogsId));
  }

  @Test
  @DisplayName("followArtistEntityExists() should return true if the given combination from user id and artist discogs id exists")
  void follow_artist_entity_exists_should_return_true_for_existing_entity(){
    // given
    FollowArtistDto followArtistDto = new FollowArtistDto(userId, artistDiscogsId);

    when(followedArtistsRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(anyLong(), anyLong())).thenReturn(true);

    // when
    boolean result = followArtistService.followArtistEntityExists(followArtistDto);

    // then
    assertThat(result).isTrue();

    verify(followedArtistsRepository, times(1)).existsFollowedArtistEntityByUserIdAndArtistDiscogsId(userId, artistDiscogsId);
  }

  @Test
  @DisplayName("followArtistEntityExists() should return false if the given combination from user id and artist discogs id does not exist")
  void follow_artist_entity_exists_should_return_false_for_not_existing_entity(){
    // given
    FollowArtistDto followArtistDto = new FollowArtistDto(userId, artistDiscogsId);

    when(followedArtistsRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(anyLong(), anyLong())).thenReturn(false);

    // when
    boolean result = followArtistService.followArtistEntityExists(followArtistDto);

    // then
    assertThat(result).isFalse();

    verify(followedArtistsRepository, times(1)).existsFollowedArtistEntityByUserIdAndArtistDiscogsId(userId, artistDiscogsId);
  }
}
