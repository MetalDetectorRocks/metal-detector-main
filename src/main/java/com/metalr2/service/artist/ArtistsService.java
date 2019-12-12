package com.metalr2.service.artist;

import com.metalr2.web.dto.ArtistDto;

import java.util.List;
import java.util.Optional;

public interface ArtistsService {

  Optional<ArtistDto> findByArtistDiscogsId(long artistDiscogsId);

  List<ArtistDto> findAllByArtistDiscogsIdIn(long... artistDiscogsIds);

  boolean existsByArtistDiscogsId(long artistDiscogsId);

}
