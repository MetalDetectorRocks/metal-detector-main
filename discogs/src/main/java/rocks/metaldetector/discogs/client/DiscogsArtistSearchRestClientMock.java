package rocks.metaldetector.discogs.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;

import java.util.Optional;

@Service
@Profile({"mockmode"})
public class DiscogsArtistSearchRestClientMock implements DiscogsArtistSearchRestClient {

  @Override
  public Optional<DiscogsArtistSearchResultDto> searchByName(String artistQueryString, int pageNumber, int pageSize) {
    return Optional.empty();
  }

  @Override
  public Optional<DiscogsArtistDto> searchById(long artistId) {
    return Optional.empty();
  }
}

// ToDo DanielW: Test and implement