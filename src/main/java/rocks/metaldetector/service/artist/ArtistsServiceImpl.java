package rocks.metaldetector.service.artist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.model.artist.ArtistEntity;
import rocks.metaldetector.model.artist.ArtistsRepository;
import rocks.metaldetector.model.artist.FollowedArtistEntity;
import rocks.metaldetector.model.artist.FollowedArtistsRepository;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.discogs.DiscogsArtistSearchRestClient;
import rocks.metaldetector.web.dto.ArtistDto;
import rocks.metaldetector.web.dto.NameSearchResultDto;
import rocks.metaldetector.web.dto.NameSearchResultsDto;
import rocks.metaldetector.web.dto.discogs.artist.DiscogsArtist;
import rocks.metaldetector.web.dto.discogs.search.DiscogsArtistSearchResultContainer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistsServiceImpl implements ArtistsService {

  private final ArtistsRepository artistsRepository;
  private final FollowedArtistsRepository followedArtistsRepository;
  private final DiscogsArtistSearchRestClient artistSearchClient;
  private final CurrentUserSupplier currentUserSupplier;

  @Autowired
  public ArtistsServiceImpl(ArtistsRepository artistsRepository, FollowedArtistsRepository followedArtistsRepository,
                            DiscogsArtistSearchRestClient artistSearchClient, CurrentUserSupplier currentUserSupplier) {
    this.artistsRepository = artistsRepository;
    this.followedArtistsRepository = followedArtistsRepository;
    this.artistSearchClient = artistSearchClient;
    this.currentUserSupplier = currentUserSupplier;
  }

  @Override
  public Optional<ArtistDto> findArtistByDiscogsId(long discogsId) {
    return artistsRepository.findByArtistDiscogsId(discogsId)
        .map(this::createArtistDto);
  }

  @Override
  public List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds) {
    List<ArtistEntity> artistEntities = artistsRepository.findAllByArtistDiscogsIdIn(discogsIds);
    return artistEntities.stream()
        .map(this::createArtistDto)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByDiscogsId(long discogsId) {
    return artistsRepository.existsByArtistDiscogsId(discogsId);
  }

  @Override
  @Transactional
  public boolean followArtist(long discogsId) {
    boolean artistAlreadyExistsOrSavedSuccessfully = fetchAndSaveArtist(discogsId);
    if (artistAlreadyExistsOrSavedSuccessfully) {
      FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(currentUserSupplier.get().getPublicId(), discogsId);
      followedArtistsRepository.save(followedArtistEntity);
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  public boolean unfollowArtist(long discogsId) {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndDiscogsId(
        currentUserSupplier.get().getPublicId(), discogsId);

    optionalFollowedArtistEntity.ifPresent(followedArtistsRepository::delete);
    return optionalFollowedArtistEntity.isPresent();
  }

  @Override
  public boolean isFollowed(long discogsId) {
    return followedArtistsRepository.existsByPublicUserIdAndDiscogsId(currentUserSupplier.get().getPublicId(), discogsId);
  }

  @Override
  public List<ArtistDto> findFollowedArtistsPerUser(String publicUserId) {
    List<FollowedArtistEntity> followedArtistEntities = followedArtistsRepository.findByPublicUserId(publicUserId);
    return mapArtistDtos(followedArtistEntities);
  }

  @Override
  public List<ArtistDto> findFollowedArtistsPerUser(String publicUserId, Pageable pageable) {
    List<FollowedArtistEntity> followedArtistEntities = followedArtistsRepository.findByPublicUserId(publicUserId, pageable);
    return mapArtistDtos(followedArtistEntities);
  }

  @Override
  public List<ArtistDto> findFollowedArtistsForCurrentUser() {
    return findFollowedArtistsPerUser(currentUserSupplier.get().getPublicId());
  }

  @Override
  public List<ArtistDto> findFollowedArtistsForCurrentUser(Pageable pageable) {
    return findFollowedArtistsPerUser(currentUserSupplier.get().getPublicId(), pageable);
  }

  @Override
  public long countFollowedArtistsPerUser(String publicUserId) {
    return followedArtistsRepository.countByPublicUserId(publicUserId);
  }

  @Override
  public long countFollowedArtistsForCurrentUser() {
    return countFollowedArtistsPerUser(currentUserSupplier.get().getPublicId());
  }

  @Override
  public Optional<NameSearchResultsDto> searchDiscogsByName(String artistQueryString, Pageable pageable) {
    Optional<DiscogsArtistSearchResultContainer> responseOptional = artistSearchClient.searchByName(artistQueryString, pageable);
    return responseOptional.map(this::mapNameSearchResponse);
  }

  @Override
  @Transactional
  public boolean fetchAndSaveArtist(long discogsId) {
    if (artistsRepository.existsByArtistDiscogsId(discogsId)) {
      return true;
    }

    Optional<DiscogsArtist> artistOptional = artistSearchClient.searchById(discogsId);

    if (artistOptional.isEmpty()) {
      return false;
    }

    ArtistEntity artistEntity = mapArtistEntity(artistOptional.get());
    artistsRepository.save(artistEntity);
    return true;
  }

  private NameSearchResultsDto mapNameSearchResponse(DiscogsArtistSearchResultContainer artistSearchResults) {
    List<Long> alreadyFollowedArtists = findFollowedArtistsForCurrentUser().stream().map(ArtistDto::getDiscogsId)
        .collect(Collectors.toList());

    List<NameSearchResultDto> dtoSearchResults = artistSearchResults.getResults().stream()
        .map(artistSearchResult -> new NameSearchResultDto(artistSearchResult.getThumb(), artistSearchResult.getId(),
                                                           artistSearchResult.getTitle(), alreadyFollowedArtists.contains(artistSearchResult.getId())))
        .collect(Collectors.toList());

    return new NameSearchResultsDto(dtoSearchResults, artistSearchResults.getDiscogsPagination().getItemsTotal());
  }

  private ArtistDto createArtistDto(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .discogsId(artistEntity.getArtistDiscogsId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .build();
  }

  private ArtistEntity mapArtistEntity(DiscogsArtist artist) {
    String thumb = artist.getDiscogsImages() != null && artist.getDiscogsImages().size() > 0 ? artist.getDiscogsImages().get(0).getResourceUrl()
                                                                                             : null;
    return new ArtistEntity(artist.getId(), artist.getName(), thumb);
  }

  private List<ArtistDto> mapArtistDtos(List<FollowedArtistEntity> followedArtistEntities) {
    return findAllArtistsByDiscogsIds(followedArtistEntities.stream().mapToLong(FollowedArtistEntity::getDiscogsId).toArray());
  }
}
