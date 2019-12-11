package com.metalr2.service.artist;

import com.metalr2.model.artist.ArtistEntity;
import com.metalr2.model.artist.ArtistsRepository;
import com.metalr2.web.dto.ArtistDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistsServiceImpl implements ArtistsService {

  private final ArtistsRepository artistsRepository;

  @Autowired
  public ArtistsServiceImpl(ArtistsRepository artistsRepository){
    this.artistsRepository = artistsRepository;
  }

  @Override
  public Optional<ArtistDto> findByArtistDiscogsId(long artistDiscogsId) {
    Optional<ArtistEntity> artistEntityOptional = artistsRepository.findByArtistDiscogsId(artistDiscogsId);

    if (artistEntityOptional.isEmpty()){
      return Optional.empty();
    }

    ArtistEntity artistEntity = artistEntityOptional.get();
    return Optional.of(ArtistDto.builder()
            .artistDiscogsId(artistEntity.getArtistDiscogsId())
            .artistName(artistEntity.getArtistName())
            .thumb(artistEntity.getThmub())
            .build());
  }

  @Override
  public List<ArtistDto> findAllByArtistDiscogsIds(long... artistDiscogsIds) {
    List<ArtistEntity> artistEntities = artistsRepository.findAllByArtistDiscogsIds(artistDiscogsIds);
    return artistEntities.stream()
            .map(artistEntity -> ArtistDto.builder()
              .artistDiscogsId(artistEntity.getArtistDiscogsId())
              .artistName(artistEntity.getArtistName())
              .thumb(artistEntity.getThmub())
              .build())
            .collect(Collectors.toList());
  }

  @Override
  public boolean existsByArtistDiscogsId(long artistDiscogsId) {
    return artistsRepository.existsByArtistDiscogsId(artistDiscogsId);
  }
}
