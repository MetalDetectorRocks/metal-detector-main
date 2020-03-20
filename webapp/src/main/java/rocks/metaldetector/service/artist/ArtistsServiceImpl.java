package rocks.metaldetector.service.artist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.domain.DiscogsArtistSearchRestClient;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsSearchResultDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistsRepository;
import rocks.metaldetector.persistence.domain.artist.FollowedArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowedArtistsRepository;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.web.dto.ArtistDto;
import rocks.metaldetector.web.dto.response.Pagination;
import rocks.metaldetector.web.dto.response.SearchResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static rocks.metaldetector.web.dto.response.SearchResponse.SearchResult;

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
  public Optional<SearchResponse> searchDiscogsByName(String artistQueryString, Pageable pageable) {
    Optional<DiscogsSearchResultDto<DiscogsArtistSearchResultDto>> responseOptional = artistSearchClient.searchByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());

    return Optional.empty(); // ToDo DanieW: Hier das richtige DiscogsSearchResultDto zurückgeben
  }

  @Override
  @Transactional
  public boolean fetchAndSaveArtist(long discogsId) {
    if (artistsRepository.existsByArtistDiscogsId(discogsId)) {
      return true;
    }

    Optional<DiscogsArtistDto> artistOptional = artistSearchClient.searchById(discogsId);
    artistOptional.ifPresent(artist -> {
      ArtistEntity artistEntity = new ArtistEntity(artist.getId(), artist.getName(), artist.getImageUrl());
      artistsRepository.save(artistEntity);
    });

    return artistOptional.isPresent();
  }

  // ToDo DanielW: Das Mapping muss ins discogs Modul
  private SearchResponse mapNameSearchResult(DiscogsArtistSearchResultContainer artistSearchResults) {
    DiscogsPagination discogsPagination = artistSearchResults.getPagination();

    int itemsPerPage = discogsPagination.getItemsPerPage();

    List<Long> alreadyFollowedArtists = findFollowedArtistsForCurrentUser().stream().map(ArtistDto::getDiscogsId)
        .collect(Collectors.toList());

    List<SearchResult> dtoSearchResults = artistSearchResults.getResults().stream()
        .map(artistSearchResult -> new SearchResult(artistSearchResult.getThumb(), artistSearchResult.getId(),
                                                    artistSearchResult.getTitle(), alreadyFollowedArtists.contains(artistSearchResult.getId())))
        .collect(Collectors.toList());

    // Discogs works with page "1" being the first page, see https://trello.com/c/euiR6RPp
    Pagination pagination = new Pagination(discogsPagination.getItemsTotal(), discogsPagination.getCurrentPage() - 1, itemsPerPage);

    return new SearchResponse(dtoSearchResults, pagination);
  }

  private ArtistDto createArtistDto(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .discogsId(artistEntity.getArtistDiscogsId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .build();
  }

  private List<ArtistDto> mapArtistDtos(List<FollowedArtistEntity> followedArtistEntities) {
    return findAllArtistsByDiscogsIds(followedArtistEntities.stream().mapToLong(FollowedArtistEntity::getDiscogsId).toArray());
  }

}
