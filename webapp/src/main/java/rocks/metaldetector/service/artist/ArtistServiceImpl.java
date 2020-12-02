package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@Service
@AllArgsConstructor
public class ArtistServiceImpl implements ArtistService {

  private final ArtistRepository artistRepository;
  private final ArtistTransformer artistTransformer;

  @Override
  public Optional<ArtistDto> findArtistByExternalId(String externalId, ArtistSource source) {
    return artistRepository.findByExternalIdAndSource(externalId, source)
        .map(artistTransformer::transform);
  }

  @Override
  public List<ArtistDto> findAllArtistsByExternalIds(List<String> externalIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByExternalIdIn(externalIds);
    return artistEntities.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByExternalId(String externalId, ArtistSource source) {
    return artistRepository.existsByExternalIdAndSource(externalId, source);
  }

  @Override
  public void persistSpotifyArtists(List<SpotifyArtistDto> spotifyArtistDtos) {
    List<ArtistEntity> artistEntities = spotifyArtistDtos.stream()
        .map(artistDto -> new ArtistEntity(artistDto.getId(), artistDto.getName(), artistDto.getImageUrl(), SPOTIFY))
        .collect(Collectors.toList());
    artistRepository.saveAll(artistEntities);
  }

  @Override
  public List<String> findNewArtistIds(List<String> artistIds) {
    List<String> existingArtistIds = artistRepository.findAllByExternalIdIn(artistIds).stream()
        .map(ArtistEntity::getExternalId)
        .collect(Collectors.toList());
    return artistIds.stream()
        .filter(id -> !existingArtistIds.contains(id))
        .collect(Collectors.toList());
  }
}
