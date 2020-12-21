package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyArtistImportResult;

public interface SpotifyUserLibraryClient {

  SpotifyAlbumImportResult fetchLikedAlbums(String token, int offset);

  SpotifyArtistImportResult fetchFollowedArtists(String token, int offset);
}
