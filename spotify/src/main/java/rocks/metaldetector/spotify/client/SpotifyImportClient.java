package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.imports.SpotfiyAlbumImportResult;

public interface SpotifyImportClient {

  SpotfiyAlbumImportResult importAlbums(String token, int offset);
}
