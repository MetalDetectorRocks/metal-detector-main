package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistsServiceImpl implements ArtistsService {

  private final ArtistRepository artistRepository;
  private final UserRepository userRepository;
  private final DiscogsService discogsService;
  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;
  private final ArtistTransformer artistTransformer;

  @Override
  public Optional<ArtistDto> findArtistByExternalId(String externalId) {
    return artistRepository.findByExternalId(externalId)
        .map(artistTransformer::transform);
  }

  @Override
  public List<ArtistDto> findAllArtistsByExternalIds(Collection<String> externalIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByExternalIdIn(externalIds);
    return artistEntities.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByExternalId(String externalId) {
    return artistRepository.existsByExternalId(externalId);
  }

  @Override
  @Transactional
  public DiscogsArtistSearchResultDto searchDiscogsByName(String artistQueryString, Pageable pageable) {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity userEntity = userRepository.findByPublicId(publicUserId).orElseThrow(() ->
            new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );
    DiscogsArtistSearchResultDto result = discogsService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
    result.getSearchResults().forEach(artist -> artist.setFollowed(userEntity.isFollowing(artist.getId())));

    return result;
  }
}
