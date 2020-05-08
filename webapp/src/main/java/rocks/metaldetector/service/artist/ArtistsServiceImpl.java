package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.security.CurrentUserSupplier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistsServiceImpl implements ArtistsService {

  private final ArtistRepository artistRepository;
  private final DiscogsService discogsService;
  private final CurrentUserSupplier currentUserSupplier;
  private final ArtistDtoTransformer artistDtoTransformer;

  @Override
  public Optional<ArtistDto> findArtistByDiscogsId(long discogsId) {
    return artistRepository.findByArtistDiscogsId(discogsId)
        .map(artistDtoTransformer::transform);
  }

  @Override
  public List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByArtistDiscogsIdIn(discogsIds);
    return artistEntities.stream()
        .map(artistDtoTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByDiscogsId(long discogsId) {
    return artistRepository.existsByArtistDiscogsId(discogsId);
  }

  @Override
  public DiscogsArtistSearchResultDto searchDiscogsByName(String artistQueryString, Pageable pageable) {
    List<Long> alreadyFollowedArtists = currentUserSupplier.get().getFollowedArtists().stream()
                                                                                      .map(ArtistEntity::getArtistDiscogsId)
                                                                                      .collect(Collectors.toList());

    DiscogsArtistSearchResultDto result = discogsService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
    result.getSearchResults().forEach(artist -> artist.setFollowed(alreadyFollowedArtists.contains(artist.getId())));

    return result;
  }
}
