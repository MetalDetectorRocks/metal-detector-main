package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.service.artist.transformer.ArtistDtoTransformer;
import rocks.metaldetector.service.artist.transformer.ArtistEntityTransformer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistServiceImpl implements ArtistService {

  private final ArtistRepository artistRepository;
  private final ArtistEntityTransformer artistEntityTransformer;
  private final ArtistDtoTransformer artistDtoTransformer;

  @Override
  public Optional<ArtistDto> findArtistByExternalId(String externalId, ArtistSource source) {
    return artistRepository.findByExternalIdAndSource(externalId, source)
        .map(artistDtoTransformer::transformArtistEntity);
  }

  @Override
  public List<ArtistDto> findAllArtistsByExternalIds(List<String> externalIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByExternalIdIn(externalIds);
    return artistEntities.stream()
        .map(artistDtoTransformer::transformArtistEntity)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByExternalId(String externalId, ArtistSource source) {
    return artistRepository.existsByExternalIdAndSource(externalId, source);
  }

  @Override
  public void persistSpotifyArtists(List<SpotifyArtistDto> spotifyArtistDtos) {
    List<ArtistEntity> artistEntities = spotifyArtistDtos.stream()
        .map(artistEntityTransformer::transformSpotifyArtistDto)
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
