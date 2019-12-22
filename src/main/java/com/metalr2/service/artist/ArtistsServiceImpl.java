package com.metalr2.service.artist;

import com.metalr2.model.artist.ArtistEntity;
import com.metalr2.model.artist.ArtistsRepository;
import com.metalr2.model.artist.FollowedArtistEntity;
import com.metalr2.model.artist.FollowedArtistsRepository;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClient;
import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.discogs.artist.DiscogsArtist;
import com.metalr2.web.dto.discogs.artist.DiscogsMember;
import com.metalr2.web.dto.discogs.misc.DiscogsImage;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.DiscogsPagination;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.Pagination;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
  public Optional<ArtistDto> findByArtistDiscogsId(long discogsId) {
    Optional<ArtistEntity> artistEntityOptional = artistsRepository.findByArtistDiscogsId(discogsId);

    if (artistEntityOptional.isEmpty()) {
      return Optional.empty();
    }

    ArtistEntity artistEntity = artistEntityOptional.get();
    return Optional.of(createArtistDto(artistEntity));
  }

  @Override
  public List<ArtistDto> findAllByArtistDiscogsIdIn(long... discogsIds) {
    List<ArtistEntity> artistEntities = artistsRepository.findAllByArtistDiscogsIdIn(discogsIds);
    return artistEntities.stream()
            .map(this::createArtistDto)
            .collect(Collectors.toList());
  }

  @Override
  public boolean existsByArtistDiscogsId(long discogsId) {
    return artistsRepository.existsByArtistDiscogsId(discogsId);
  }

  @Override
  @Transactional
  public boolean followArtist(long discogsId) {
    Optional<DiscogsArtist> artistOptional = artistSearchClient.searchById(discogsId);

    if (artistOptional.isEmpty()) {
      return false;
    }

    FollowedArtistEntity followedArtistEntity       = new FollowedArtistEntity(currentUserSupplier.get().getPublicId(),
                                                                              artistOptional.get().getName(), discogsId);
    FollowedArtistEntity savedFollowedArtistEntity  = followedArtistsRepository.save(followedArtistEntity);

    return true;
  }

  @Override
  @Transactional
  public boolean unfollowArtist(long discogsId) {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(
        currentUserSupplier.get().getPublicId(), discogsId);

    if (optionalFollowedArtistEntity.isEmpty()){
      return false;
    }

    FollowedArtistEntity followedArtistEntity = optionalFollowedArtistEntity.get();
    followedArtistsRepository.delete(followedArtistEntity);
    return true;
  }

  @Override
  public boolean exists(long discogsId) {
    return followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(currentUserSupplier.get().getPublicId(), discogsId);
  }

  @Override
  public List<FollowArtistDto> findPerUser(String publicUserId) {
    return followedArtistsRepository.findAllByPublicUserId(publicUserId).stream()
        .map(entity -> mapper.map(entity,FollowArtistDto.class)).collect(Collectors.toList());
  }

  @Override
  public Optional<ArtistNameSearchResponse> searchDiscogsByName(String artistQueryString, int page, int size) {
    Optional<DiscogsArtistSearchResultContainer> responseOptional = artistSearchClient.searchByName(artistQueryString, page, size);

    if (responseOptional.isEmpty()) {
      return Optional.empty();
    }

    ArtistNameSearchResponse response = mapNameSearchResult(responseOptional.get());
    return Optional.of(response);
  }

  @Override
  public Optional<ArtistDetailsResponse> searchDiscogsById(long artistId) {
    Optional<DiscogsArtist> responseOptional = artistSearchClient.searchById(artistId);

    if (responseOptional.isEmpty()) {
      return Optional.empty();
    }

    ArtistDetailsResponse response = mapDetailsSearchResult(responseOptional.get());
    return Optional.of(response);
  }

  private ArtistNameSearchResponse mapNameSearchResult(DiscogsArtistSearchResultContainer artistSearchResults) {
    DiscogsPagination discogsPagination = artistSearchResults.getDiscogsPagination();

    int itemsPerPage = discogsPagination.getItemsPerPage();

    Set<Long> alreadyFollowedArtists = findPerUser(currentUserSupplier.get().getPublicId()).stream().map(FollowArtistDto::getArtistDiscogsId)
        .collect(Collectors.toSet());

    List<ArtistNameSearchResponse.ArtistSearchResult> dtoArtistSearchResults = artistSearchResults.getResults().stream()
        .map(artistSearchResult -> new ArtistNameSearchResponse.ArtistSearchResult(artistSearchResult.getThumb(), artistSearchResult.getId(),
                                                                                   artistSearchResult.getTitle(), alreadyFollowedArtists.contains(artistSearchResult.getId())))
        .collect(Collectors.toList());

    Pagination pagination = new Pagination(discogsPagination.getPagesTotal(), discogsPagination.getCurrentPage(), itemsPerPage);

    return new ArtistNameSearchResponse(dtoArtistSearchResults, pagination);
  }

  private ArtistDetailsResponse mapDetailsSearchResult(DiscogsArtist discogsArtist) {
    String artistProfile      = discogsArtist.getProfile().isEmpty()      ? null : discogsArtist.getProfile();
    List<String> activeMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(DiscogsMember::isActive).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> formerMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(discogsMember -> !discogsMember.isActive()).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> images       = discogsArtist.getDiscogsImages()  == null ? null : discogsArtist.getDiscogsImages().stream().map(DiscogsImage::getResourceUrl).collect(Collectors.toList());
    boolean isFollowed        = exists(discogsArtist.getId());
    return new ArtistDetailsResponse(discogsArtist.getName(), discogsArtist.getId(), artistProfile, activeMember, formerMember, images, isFollowed);
  }

  private ArtistDto createArtistDto(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .artistDiscogsId(artistEntity.getArtistDiscogsId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .build();
  }

}
