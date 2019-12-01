package com.metalr2.service.discogs;

import com.metalr2.web.dto.discogs.artist.DiscogsArtist;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;

import java.util.Optional;

public interface DiscogsArtistSearchRestClient {

  Optional<DiscogsArtistSearchResultContainer> searchByName(String artistQueryString, int page, int size);

  Optional<DiscogsArtist> searchById(long artistId);

}
