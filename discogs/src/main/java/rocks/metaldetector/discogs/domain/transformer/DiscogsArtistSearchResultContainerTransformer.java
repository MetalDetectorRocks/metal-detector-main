package rocks.metaldetector.discogs.domain.transformer;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsSearchResultDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscogsArtistSearchResultContainerTransformer {

  private final ModelMapper modelMapper;

  public DiscogsArtistSearchResultContainerTransformer() {
    this.modelMapper = new ModelMapper();
  }

  // ToDo DanielW: Tests
  public DiscogsSearchResultDto<DiscogsArtistSearchResultDto> transform(DiscogsArtistSearchResultContainer container) {
    DiscogsSearchResultDto<DiscogsArtistSearchResultDto> result = new DiscogsSearchResultDto<>();

    DiscogsPagination pagination = container.getPagination();
    result.setCurrentPage(pagination.getCurrentPage());
    result.setItemsPerPage(pagination.getItemsPerPage());
    result.setItemsTotal(pagination.getItemsTotal());
    result.setPagesTotal(pagination.getPagesTotal());

    List<DiscogsArtistSearchResultDto> entries = container.getResults().stream()
            .map(discogsArtistSearchResult -> modelMapper.map(discogsArtistSearchResult, DiscogsArtistSearchResultDto.class))
            .collect(Collectors.toList());

    result.setEntries(entries);

    return result;
  }
}
