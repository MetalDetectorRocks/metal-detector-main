package com.metalr2.service.followArtist;

import com.metalr2.model.followArtist.FollowedArtistRepository;
import com.metalr2.web.dto.FollowArtistsDto;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowArtistServiceTest implements WithAssertions {

  private static final long userId          = 1L;
  private static final long artistDiscogsId = 252211L;

  @Mock
  private FollowedArtistRepository followedArtistRepository;

  @InjectMocks
  private FollowArtistServiceImpl followArtistService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    reset(followedArtistRepository);
  }

  @Test
  @DisplayName("")
  void follow_artist(){
    // given
    FollowArtistsDto followArtistsDto = new FollowArtistsDto(userId, artistDiscogsId);

    // when
    FollowArtistsDto savedFollowArtistDto = followArtistService.followArtist(followArtistsDto);

    // then
    assertThat(savedFollowArtistDto).isNotNull();
    assertThat(savedFollowArtistDto.getUserId()).isEqualTo(userId);
    assertThat(savedFollowArtistDto.getArtistDiscogsId()).isEqualTo(artistDiscogsId);

  }

}