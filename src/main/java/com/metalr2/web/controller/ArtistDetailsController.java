package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.artist.Artist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
public class ArtistDetailsController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private static final String DEFAULT_ARTIST_ID = "0";
  private static final String DEFAULT_ARTIST_NAME = "";

  @Autowired
  public ArtistDetailsController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
  }

  @GetMapping({Endpoints.Frontend.ARTIST_DETAILS})
  public ModelAndView handleSearchRequest(@RequestParam(name = "artistName", defaultValue = DEFAULT_ARTIST_NAME) String artistName,
                                          @RequestParam(name = "id", defaultValue = DEFAULT_ARTIST_ID) long artistId) {
    return createArtistDetailsModelAndView(artistName, artistId);
  }

  private ModelAndView createArtistDetailsModelAndView(String artistName, long artistId) {
    Optional<Artist> artistOptional = artistSearchRestClient.searchForArtistById(artistId);

    if (artistOptional.isEmpty()) {
      return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, "totalPages", "0");
    }

    Artist artist = artistOptional.get();

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", artistName);
    viewModel.put("artistId", artist.getId());
    viewModel.put("artistImages", artist.getImages());
    viewModel.put("artistProfile", artist.getProfile());
//    viewModel.put("artistMembers", artist.getMembers());

    return new ModelAndView(ViewNames.Frontend.ARTIST_DETAILS, viewModel);
  }
}
