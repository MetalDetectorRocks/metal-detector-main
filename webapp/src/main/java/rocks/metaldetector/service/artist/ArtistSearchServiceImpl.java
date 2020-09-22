package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.transformer.ArtistSearchResponseTransformer;

@Service
@AllArgsConstructor
public class ArtistSearchServiceImpl implements ArtistSearchService {

    private final UserRepository userRepository;
    private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

    private final DiscogsService discogsService;
    private final SpotifyService spotifyService;
    private final ArtistSearchResponseTransformer responseTransformer;

    @Override
    @Transactional
    public ArtistSearchResponse searchDiscogsByName(String artistQueryString, Pageable pageable) {
        DiscogsArtistSearchResultDto result = discogsService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        ArtistSearchResponse searchResponse = responseTransformer.transformDiscogs(result);
        UserEntity userEntity = getUserEntity();
        searchResponse.getSearchResults().forEach(artist -> artist.setFollowed(userEntity.isFollowing(artist.getId())));
        return searchResponse;
    }

    @Override
    @Transactional
    public ArtistSearchResponse searchSpotifyByName(String artistQueryString, Pageable pageable) {
        SpotifyArtistSearchResultDto result = spotifyService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        ArtistSearchResponse searchResponse = responseTransformer.transformSpotify(result);
        UserEntity userEntity = getUserEntity();
        searchResponse.getSearchResults().forEach(artist -> artist.setFollowed(userEntity.isFollowing(artist.getId())));
        return searchResponse;
    }

    private UserEntity getUserEntity() {
        String publicUserId = currentPublicUserIdSupplier.get();
        return userRepository.findByPublicId(publicUserId).orElseThrow(
                () -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
        );
    }
}
