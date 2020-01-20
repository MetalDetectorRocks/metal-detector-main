package com.metalr2.service.artist;

import com.metalr2.model.artist.ArtistEntity;
import com.metalr2.model.artist.ArtistsRepository;
import com.metalr2.model.artist.FollowedArtistEntity;
import com.metalr2.model.artist.FollowedArtistsRepository;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClient;
import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.discogs.artist.DiscogsArtist;
import com.metalr2.web.dto.discogs.artist.DiscogsMember;
import com.metalr2.web.dto.discogs.misc.DiscogsImage;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.DiscogsPagination;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.Pagination;
import com.metalr2.web.dto.response.SearchResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.metalr2.web.dto.response.SearchResponse.SearchResult;

@Service
public class ArtistsServiceImpl implements ArtistsService {

  private final ArtistsRepository artistsRepository;
  private final FollowedArtistsRepository followedArtistsRepository;
  private final DiscogsArtistSearchRestClient artistSearchClient;
  private final CurrentUserSupplier currentUserSupplier;
  private final ModelMapper mapper;

  @Autowired
  public ArtistsServiceImpl(ArtistsRepository artistsRepository, FollowedArtistsRepository followedArtistsRepository,
                            DiscogsArtistSearchRestClient artistSearchClient, CurrentUserSupplier currentUserSupplier) {
    this.artistsRepository = artistsRepository;
    this.followedArtistsRepository = followedArtistsRepository;
    this.artistSearchClient = artistSearchClient;
    this.currentUserSupplier = currentUserSupplier;
    this.mapper = new ModelMapper();
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
  public Optional<SearchResponse> searchDiscogsByName(String artistQueryString, int page, int size) {
    Optional<DiscogsArtistSearchResultContainer> responseOptional = artistSearchClient.searchByName(artistQueryString, page, size);
    return responseOptional.map(this::mapNameSearchResult);
  }

  @Override
  public Optional<ArtistDetailsResponse> searchDiscogsById(long discogsId) {
    Optional<DiscogsArtist> responseOptional = artistSearchClient.searchById(discogsId);
    return responseOptional.map(this::mapDetailsSearchResult);
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

  private SearchResponse mapNameSearchResult(DiscogsArtistSearchResultContainer artistSearchResults) {
    DiscogsPagination discogsPagination = artistSearchResults.getDiscogsPagination();

    int itemsPerPage = discogsPagination.getItemsPerPage();

    List<Long> alreadyFollowedArtists = findFollowedArtistsForCurrentUser().stream().map(ArtistDto::getDiscogsId)
        .collect(Collectors.toList());

    List<SearchResult> dtoSearchResults = artistSearchResults.getResults().stream()
        .map(artistSearchResult -> new SearchResult(artistSearchResult.getThumb(), artistSearchResult.getId(),
                                                    artistSearchResult.getTitle(), alreadyFollowedArtists.contains(artistSearchResult.getId())))
        .collect(Collectors.toList());

    Pagination pagination = new Pagination(discogsPagination.getItemsTotal(), discogsPagination.getCurrentPage(), itemsPerPage);

    return new SearchResponse(dtoSearchResults, pagination);
  }

  private ArtistDetailsResponse mapDetailsSearchResult(DiscogsArtist discogsArtist) {
    String artistProfile      = discogsArtist.getProfile().isEmpty()      ? null : discogsArtist.getProfile();
    List<String> activeMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(DiscogsMember::isActive).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> formerMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(discogsMember -> !discogsMember.isActive()).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> images       = discogsArtist.getDiscogsImages()  == null ? null : discogsArtist.getDiscogsImages().stream().map(DiscogsImage::getResourceUrl).collect(Collectors.toList());
    boolean isFollowed        = isFollowed(discogsArtist.getId());
    return new ArtistDetailsResponse(discogsArtist.getName(), discogsArtist.getId(), artistProfile, activeMember, formerMember, images, isFollowed);
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
