package rocks.metaldetector.service.artist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.FollowedArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowedArtistRepository;
import rocks.metaldetector.security.CurrentUserSupplier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistsServiceImpl implements ArtistsService {

  private final ArtistRepository artistRepository;
  private final FollowedArtistRepository followedArtistRepository;
  private final DiscogsService discogsService;
  private final CurrentUserSupplier currentUserSupplier;

  @Autowired
  public ArtistsServiceImpl(ArtistRepository artistRepository, FollowedArtistRepository followedArtistRepository,
                            DiscogsService discogsService, CurrentUserSupplier currentUserSupplier) {
    this.artistRepository = artistRepository;
    this.followedArtistRepository = followedArtistRepository;
    this.discogsService = discogsService;
    this.currentUserSupplier = currentUserSupplier;
  }

  @Override
  public Optional<ArtistDto> findArtistByDiscogsId(long discogsId) {
    return artistRepository.findByArtistDiscogsId(discogsId)
        .map(this::createArtistDto);
  }

  @Override
  public List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByArtistDiscogsIdIn(discogsIds);
    return artistEntities.stream()
            .map(this::createArtistDto)
            .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByDiscogsId(long discogsId) {
    return artistRepository.existsByArtistDiscogsId(discogsId);
  }

  @Override
  @Transactional
  public void followArtist(long discogsId) {
    if (! isFollowed(discogsId)) {
      fetchAndSaveArtist(discogsId);
      FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(currentUserSupplier.get().getPublicId(), discogsId);
      followedArtistRepository.save(followedArtistEntity);
    }
  }

  @Override
  @Transactional
  public void unfollowArtist(long discogsId) {
    if (isFollowed(discogsId)) {
      followedArtistRepository.deleteByPublicUserIdAndDiscogsId(currentUserSupplier.get().getPublicId(), discogsId);
    }
  }

  @Override
  public boolean isFollowed(long discogsId) {
    return followedArtistRepository.existsByPublicUserIdAndDiscogsId(currentUserSupplier.get().getPublicId(), discogsId);
  }

  @Override
  public List<ArtistDto> findFollowedArtistsPerUser(String publicUserId) {
    List<FollowedArtistEntity> followedArtistEntities = followedArtistRepository.findByPublicUserId(publicUserId);
    return mapArtistDtos(followedArtistEntities);
  }

  @Override
  public List<ArtistDto> findFollowedArtistsPerUser(String publicUserId, Pageable pageable) {
    List<FollowedArtistEntity> followedArtistEntities = followedArtistRepository.findByPublicUserId(publicUserId, pageable);
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
    return followedArtistRepository.countByPublicUserId(publicUserId);
  }

  @Override
  public long countFollowedArtistsForCurrentUser() {
    return countFollowedArtistsPerUser(currentUserSupplier.get().getPublicId());
  }

  @Override
  public DiscogsArtistSearchResultDto searchDiscogsByName(String artistQueryString, Pageable pageable) {
    List<Long> alreadyFollowedArtists = findFollowedArtistsForCurrentUser().stream()
                                                                           .map(ArtistDto::getDiscogsId)
                                                                           .collect(Collectors.toList());

    DiscogsArtistSearchResultDto result = discogsService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
    result.getSearchResults().forEach(artist -> artist.setFollowed(alreadyFollowedArtists.contains(artist.getId())));

    return result;
  }

  @Override
  @Transactional
  public void fetchAndSaveArtist(long discogsId) {
    var artistAlreadySaved = artistRepository.existsByArtistDiscogsId(discogsId);
    if (! artistAlreadySaved) {
      DiscogsArtistDto artist = discogsService.searchArtistById(discogsId);
      ArtistEntity artistEntity = new ArtistEntity(artist.getId(), artist.getName(), artist.getImageUrl());
      artistRepository.save(artistEntity);
    }
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
