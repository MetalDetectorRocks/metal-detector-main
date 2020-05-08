package rocks.metaldetector.service.user;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistTransformer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserTransformer {

  private final ModelMapper mapper;
  private final ArtistTransformer artistTransformer;

  @Autowired
  public UserTransformer(ArtistTransformer artistTransformer) {
    this.artistTransformer = artistTransformer;
    this.mapper = new ModelMapper();
  }

  public UserDto transform(UserEntity entity) {
    UserDto userDto = mapper.map(entity, UserDto.class);
    userDto.setRole(entity.getHighestRole().getDisplayName());
    userDto.setFollowedArtists(transformFollowedArtists(entity.getFollowedArtists()));
    return userDto;
  }

  private List<ArtistDto> transformFollowedArtists(Set<ArtistEntity> followedArtistEntities) {
    return followedArtistEntities.stream().map(artistTransformer::transform).collect(Collectors.toUnmodifiableList());
  }
}
