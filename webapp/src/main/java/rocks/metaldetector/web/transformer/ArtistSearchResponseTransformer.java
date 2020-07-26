package rocks.metaldetector.web.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultEntryDto;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.api.response.ArtistSearchResponseEntryDto;

import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@Component
public class ArtistSearchResponseTransformer {

  public ArtistSearchResponse transformSpotify(SpotifyArtistSearchResultDto spotifySearchResult) {
    return ArtistSearchResponse.builder()
        .pagination(spotifySearchResult.getPagination())
        .searchResults(transformSpotifySearchResults(spotifySearchResult.getSearchResults()))
        .build();
  }

  private List<ArtistSearchResponseEntryDto> transformSpotifySearchResults(List<SpotifyArtistSearchResultEntryDto> spotifySearchResults) {
    return spotifySearchResults.stream().map(this::transformSpotifySearchResult).collect(Collectors.toList());
  }

  private ArtistSearchResponseEntryDto transformSpotifySearchResult(SpotifyArtistSearchResultEntryDto spotifySearchResult) {
    return ArtistSearchResponseEntryDto.builder()
        .id(spotifySearchResult.getId())
        .name(spotifySearchResult.getName())
        .uri(spotifySearchResult.getUri())
        .imageUrl(spotifySearchResult.getImageUrl())
        .source(SPOTIFY.getDisplayName())
        .genres(spotifySearchResult.getGenres())
        .popularity(spotifySearchResult.getPopularity())
        .build();
  }

  public ArtistSearchResponse transformDiscogs(DiscogsArtistSearchResultDto discogsSearchResult) {
    return ArtistSearchResponse.builder()
        .pagination(discogsSearchResult.getPagination())
        .searchResults(transformDiscogsSearchResults(discogsSearchResult.getSearchResults()))
        .build();
  }

  private List<ArtistSearchResponseEntryDto> transformDiscogsSearchResults(List<DiscogsArtistSearchResultEntryDto> discogsSearchResults) {
    return discogsSearchResults.stream().map(this::transformDiscogsSearchResult).collect(Collectors.toList());
  }

  private ArtistSearchResponseEntryDto transformDiscogsSearchResult(DiscogsArtistSearchResultEntryDto discogsSearchResult) {
    return ArtistSearchResponseEntryDto.builder()
        .id(String.valueOf(discogsSearchResult.getId()))
        .name(discogsSearchResult.getName())
        .uri(discogsSearchResult.getUri())
        .imageUrl(discogsSearchResult.getImageUrl())
        .source(DISCOGS.getDisplayName())
        .build();
  }
}
