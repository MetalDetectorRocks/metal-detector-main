package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.imports.SpotifyFollowedArtistsPage;
import rocks.metaldetector.spotify.api.imports.SpotifySavedAlbumsPage;

public interface SpotifyUserLibraryClient {

  SpotifySavedAlbumsPage fetchLikedAlbums(String token, int offset);

  SpotifyFollowedArtistsPage fetchFollowedArtists(String token, String nextPage);
}
