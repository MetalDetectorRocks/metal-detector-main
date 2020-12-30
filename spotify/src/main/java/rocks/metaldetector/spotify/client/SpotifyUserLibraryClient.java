package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyFollowedArtistsPage;

public interface SpotifyUserLibraryClient {

  SpotifyAlbumImportResult fetchLikedAlbums(String token, int offset);

  SpotifyFollowedArtistsPage fetchFollowedArtists(String token, String nextPage);
}
