package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.transformer.ArtistSearchResponseTransformer;

@Service
@AllArgsConstructor
public class ArtistSearchServiceImpl implements ArtistSearchService {

    private final FollowArtistService followArtistService;
    private final DiscogsService discogsService;
    private final SpotifyService spotifyService;
    private final ArtistSearchResponseTransformer responseTransformer;

    @Override
    @Transactional
    public ArtistSearchResponse searchDiscogsByName(String artistQueryString, Pageable pageable) {
        DiscogsArtistSearchResultDto result = discogsService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        ArtistSearchResponse searchResponse = responseTransformer.transformDiscogs(artistQueryString, result);
        searchResponse.getSearchResults().forEach(artist -> artist.setFollowed(
                followArtistService.isCurrentUserFollowing(artist.getId(), ArtistSource.DISCOGS))
        );
        return searchResponse;
    }

    @Override
    @Transactional
    public ArtistSearchResponse searchSpotifyByName(String artistQueryString, Pageable pageable) {
        SpotifyArtistSearchResultDto result = spotifyService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        ArtistSearchResponse searchResponse = responseTransformer.transformSpotify(artistQueryString, result);
        searchResponse.getSearchResults().forEach(artist -> artist.setFollowed(
                followArtistService.isCurrentUserFollowing(artist.getId(), ArtistSource.SPOTIFY))
        );
        return searchResponse;
    }
}
