package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.imports.SpotifyFollowedArtistsPage;
import rocks.metaldetector.spotify.api.imports.SpotifySavedAlbumsPage;

public interface SpotifyUserLibraryClient {

  SpotifySavedAlbumsPage fetchLikedAlbums(int offset);

  SpotifyFollowedArtistsPage fetchFollowedArtists(String nextPage);
}
