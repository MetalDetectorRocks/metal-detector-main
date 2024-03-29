package rocks.metaldetector.service.dashboard;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.transformer.ArtistDtoTransformer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ArtistCollector {

  private final ArtistRepository artistRepository;
  private final ArtistDtoTransformer artistDtoTransformer;
  private final FollowActionRepository followActionRepository;
  private final AuthenticationFacade authenticationFacade;

  public List<ArtistDto> collectTopFollowedArtists(int minFollower) {
    return artistRepository.findTopArtists(minFollower).stream()
        .map(artistDtoTransformer::transformTopArtist)
        .collect(Collectors.toList());
  }

  public List<ArtistDto> collectRecentlyFollowedArtists(int resultLimit) {
    AbstractUserEntity currentUser = authenticationFacade.getCurrentUser();
    return followActionRepository.findAllByUser(currentUser).stream()
        .sorted(Comparator.comparing(FollowActionEntity::getCreatedDateTime).reversed())
        .limit(resultLimit)
        .map(artistDtoTransformer::transformFollowActionEntity)
        .collect(Collectors.toList());
  }
}
