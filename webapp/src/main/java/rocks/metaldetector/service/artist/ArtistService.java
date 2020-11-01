package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;
import java.util.Optional;

public interface ArtistService {

  Optional<ArtistDto> findArtistByExternalId(String externalId, ArtistSource source);

  List<ArtistDto> findAllArtistsByExternalIds(List<String> externalIds);

  boolean existsArtistByExternalId(String externalId, ArtistSource source);

  void persistArtists(List<SpotifyArtistDto> spotifyArtistDtos);

  List<String> findNewArtistIds(List<String> artistIds);

}
