package rocks.metaldetector.web.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.support.SlicingService;
import rocks.metaldetector.web.api.response.MyArtistsResponse;

import java.util.List;

@Component
@AllArgsConstructor
public class MyArtistsResponseTransformer {

  private final SlicingService slicingService;

  public MyArtistsResponse transform(List<ArtistDto> artists, int page, int size) {
    int totalPages = (int) Math.ceil((double) artists.size() / (double) size);
    page = Math.min(page, totalPages);
    return new MyArtistsResponse(slicingService.slice(artists, page, size),
                                 new Pagination(totalPages, page, size));
  }
}
