package rocks.metaldetector.service.summary;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistTransformer;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;

@Component
@AllArgsConstructor
public class ArtistCollector {

  private final ArtistRepository artistRepository;
  private final ArtistTransformer artistTransformer;
  private final FollowActionRepository followActionRepository;
  private final UserRepository userRepository;
  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  public List<ArtistDto> collectTopFollowedArtists() {
    return artistRepository.findTopArtists(RESULT_LIMIT).stream()
        .map(artistTransformer::transform)
        .peek(artist -> artist.setFollower(artistRepository.countArtistFollower(artist.getExternalId())))
        .collect(Collectors.toList());
  }

  public List<ArtistDto> collectRecentlyFollowedArtists() {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity currentUser = userRepository.findByPublicId(publicUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!"));

    return followActionRepository.findAllByUser(currentUser).stream()
        .sorted(Comparator.comparing(FollowActionEntity::getCreatedDateTime).reversed())
        .limit(RESULT_LIMIT)
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }
}
