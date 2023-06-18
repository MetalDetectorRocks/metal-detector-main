package rocks.metaldetector.web.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistSearchResponse {

  private String query;
  private Pagination pagination;
  private List<ArtistSearchResponseEntryDto> searchResults;

  public static ArtistSearchResponse empty() {
    return ArtistSearchResponse.builder()
        .searchResults(Collections.emptyList())
        .pagination(Pagination.builder().currentPage(1).build())
        .query("")
        .build();
  }

  @ArtifactForFramework
  @JsonProperty(access = READ_ONLY)
  public String getSearchResultsTitle() {
    int amount = searchResults.size();
    int totalPages = pagination.getTotalPages();
    int itemsPerPage = pagination.getItemsPerPage();

    if (amount == 0) {
      return String.format("No result for \"%s\"", query);
    }
    else if (totalPages == 1) {
      String resultWord = amount == 1 ? "result" : "results";
      return String.format("%s %s for \"%s\"", amount, resultWord, query);
    }
    else {
      int estimatedAmountOfResults = (totalPages - 1) * itemsPerPage;
      return String.format("More than %s results for \"%s\"", estimatedAmountOfResults, query);
    }
  }
}
