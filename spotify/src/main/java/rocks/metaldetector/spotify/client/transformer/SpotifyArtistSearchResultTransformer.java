package rocks.metaldetector.spotify.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SpotifyArtistSearchResultTransformer {

  private final SpotifyArtistTransformer artistTransformer;
  private final SpotifyPaginationTransformer paginationTransformer;

  public SpotifyArtistSearchResultDto transform(SpotifyArtistSearchResultContainer searchResult) {
    return SpotifyArtistSearchResultDto.builder()
        .pagination(paginationTransformer.transform(searchResult.getArtists()))
        .searchResults(transformArtistSearchResults(searchResult.getArtists().getItems()))
        .build();
  }

  private List<SpotifyArtistDto> transformArtistSearchResults(List<SpotifyArtist> results) {
    return results.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }
}
