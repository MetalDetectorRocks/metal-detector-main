package rocks.metaldetector.service.artist;

import org.springframework.data.domain.Pageable;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;

import java.util.List;
import java.util.Optional;

public interface ArtistsService {

  Optional<ArtistDto> findArtistByDiscogsId(long discogsId);
  List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds);
  boolean existsArtistByDiscogsId(long discogsId);
  boolean fetchAndSaveArtist(long discogsId);

  boolean followArtist(long discogsId);
  boolean unfollowArtist(long discogsId);
  boolean isFollowed(long discogsId);
  List<ArtistDto> findFollowedArtistsPerUser(String publicUserId);
  List<ArtistDto> findFollowedArtistsPerUser(String publicUserId, Pageable pageable);
  List<ArtistDto> findFollowedArtistsForCurrentUser();
  List<ArtistDto> findFollowedArtistsForCurrentUser(Pageable pageable);
  long countFollowedArtistsPerUser(String publicUserId);
  long countFollowedArtistsForCurrentUser();

  DiscogsArtistSearchResultDto searchDiscogsByName(String artistQueryString, Pageable pageable);

}
