package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.imports.SpotfiyAlbumImportResult;

public interface SpotifyUserLibraryClient {

  SpotfiyAlbumImportResult fetchLikedAlbums(String token, int offset);
}
