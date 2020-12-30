package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.imports.SpotfiyTrackImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbum;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResultItem;
import rocks.metaldetector.spotify.api.imports.SpotifyFollowedArtistsPage;
import rocks.metaldetector.spotify.api.imports.SpotifyTrack;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Profile("mockmode")
@AllArgsConstructor
public class SpotifyUserLibraryClientMock implements SpotifyUserLibraryClient {

  @Override
  public SpotifyAlbumImportResult fetchLikedAlbums(String token, int offset) {
    return SpotifyAlbumImportResult.builder()
        .href("https://api.spotify.com/v1/search?query=Opeth&type=artist&offset=0&limit=20")
        .items(List.of(createARomanceWithViolence()))
        .limit(20)
        .next(null)
        .previous(null)
        .offset(0)
        .total(1)
        .build();
  }

  @Override
  public SpotifyFollowedArtistsPage fetchFollowedArtists(String token, String nextPage) {
    return SpotifyFollowedArtistsPage.builder()
        .items(List.of(createWayfarer()))
        .build();
  }

  private SpotifyAlbumImportResultItem createARomanceWithViolence() {
    return SpotifyAlbumImportResultItem.builder()
        .addedAt(LocalDateTime.now())
        .album(SpotifyAlbum.builder()
                   .albumType("album")
                   .artists(List.of(createWayfarer()))
                   .availableMarkets(List.of("DE", "US"))
                   .id("3QgcRjIIRDWkvBq9el0GGo")
                   .label("Profound Lore")
                   .name("A Romance with Violence")
                   .popularity(37)
                   .releaseDate("2020-10-16")
                   .releaseDatePrecision("day")
                   .totalTracks(7)
                   .tracks(SpotfiyTrackImportResult.builder()
                               .items(List.of(SpotifyTrack.builder()
                                                  .artists(List.of(createWayfarer()))
                                                  .discNumber(1)
                                                  .durationMs(72653)
                                                  .explicit(false)
                                                  .name("The Curtain Pulls Back")
                                                  .trackNumber(1)
                                                  .build()))
                               .build())
                   .build())
        .build();
  }

  private SpotifyArtist createWayfarer() {
    return SpotifyArtist.builder()
        .name("Wayfarer")
        .id("4HcBIH7pVbrRRwHnEqxpka")
        .build();
  }
}
