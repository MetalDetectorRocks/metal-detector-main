package rocks.metaldetector.discogs.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscogsArtistSearchResultFilter {

  public DiscogsArtistSearchResultContainer filterDiscogsSearchResults(DiscogsArtistSearchResultContainer resultContainer, String query) {
    List<DiscogsArtistSearchResult> filteredResults = resultContainer.getResults().stream()
        .filter(result -> result.getTitle().toLowerCase().contains(query.trim().toLowerCase()))
        .collect(Collectors.toList());
    resultContainer.setResults(filteredResults);
    return resultContainer;
  }
}
