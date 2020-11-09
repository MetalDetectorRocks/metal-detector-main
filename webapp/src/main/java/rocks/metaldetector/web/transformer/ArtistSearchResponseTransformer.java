package rocks.metaldetector.web.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.api.response.ArtistSearchResponseEntryDto;

import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@Component
@AllArgsConstructor
public class ArtistSearchResponseTransformer {

  private final ArtistRepository artistRepository;

  public ArtistSearchResponse transformSpotify(String query, SpotifyArtistSearchResultDto spotifySearchResult) {
    return ArtistSearchResponse.builder()
        .query(query)
        .pagination(spotifySearchResult.getPagination())
        .searchResults(transformSpotifySearchResults(spotifySearchResult.getSearchResults()))
        .build();
  }

  private List<ArtistSearchResponseEntryDto> transformSpotifySearchResults(List<SpotifyArtistDto> spotifySearchResults) {
    return spotifySearchResults.stream().map(this::transformSpotifySearchResult).collect(Collectors.toList());
  }

  private ArtistSearchResponseEntryDto transformSpotifySearchResult(SpotifyArtistDto spotifySearchResult) {
    return ArtistSearchResponseEntryDto.builder()
        .id(spotifySearchResult.getId())
        .name(spotifySearchResult.getName())
        .uri(spotifySearchResult.getUri())
        .imageUrl(spotifySearchResult.getImageUrl())
        .source(SPOTIFY.getDisplayName())
        .genres(spotifySearchResult.getGenres())
        .popularity(spotifySearchResult.getPopularity())
        .metalDetectorFollower(artistRepository.countArtistFollower(spotifySearchResult.getId()))
        .spotifyFollower(spotifySearchResult.getFollower())
        .build();
  }

  public ArtistSearchResponse transformDiscogs(String query, DiscogsArtistSearchResultDto discogsSearchResult) {
    return ArtistSearchResponse.builder()
        .query(query)
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
        .metalDetectorFollower(artistRepository.countArtistFollower(discogsSearchResult.getId()))
        .build();
  }
}
