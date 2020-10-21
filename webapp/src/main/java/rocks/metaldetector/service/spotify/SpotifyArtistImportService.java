package rocks.metaldetector.service.spotify;

import rocks.metaldetector.service.artist.ArtistDto;

import java.util.List;

public interface SpotifyArtistImportService {

  List<ArtistDto> importArtists();
}
