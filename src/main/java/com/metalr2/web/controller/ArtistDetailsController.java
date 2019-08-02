package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.artist.Artist;
import com.metalr2.web.dto.request.ArtistSearchByIdRequest;
import com.metalr2.web.dto.request.ArtistSearchByNameRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
public class ArtistDetailsController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private final String DEFAULT_ARTIST_ID = "0";

  @Autowired
  public ArtistDetailsController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
  }

  @ModelAttribute
  private ArtistSearchByNameRequest searchRequest() {
    return new ArtistSearchByNameRequest();
  }

  @GetMapping({Endpoints.Frontend.ARTIST_DETAILS})
  public ModelAndView handleSearchRequest(@RequestParam(name = "id", defaultValue = DEFAULT_ARTIST_ID) long artistId) {
    return createArtistDetailsModelAndView(artistId);
  }

  private ModelAndView createArtistDetailsModelAndView(long artistId) {
    Optional<Artist> artistOptional = artistSearchRestClient.searchForArtistById(artistId);

    if (artistOptional.isEmpty()) {
      return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, "totalPages", "0");
    }

    Artist artist = artistOptional.get();

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistId", artist.getId());
    viewModel.put("artistImages", artist.getImages());
    viewModel.put("artistProfile", artist.getProfile());
//    viewModel.put("artistMembers", artist.getMembers());

    return new ModelAndView(ViewNames.Frontend.ARTIST_DETAILS, viewModel);
  }
}
