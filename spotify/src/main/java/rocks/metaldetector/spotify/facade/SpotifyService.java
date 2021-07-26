package rocks.metaldetector.spotify.facade;

import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

import java.util.List;

public interface SpotifyService {

  SpotifyArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize);

  SpotifyArtistDto searchArtistById(String artistId);

  List<SpotifyArtistDto> searchArtistsByIds(List<String> artistIds);

  List<SpotifyAlbumDto> fetchLikedAlbums();

  List<SpotifyArtistDto> fetchFollowedArtists();
}
