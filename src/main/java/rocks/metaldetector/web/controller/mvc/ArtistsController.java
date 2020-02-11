package rocks.metaldetector.web.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;

@Controller
@RequestMapping(Endpoints.Frontend.ARTISTS)
public class ArtistsController {

  @GetMapping(path = Endpoints.Frontend.SEARCH)
  public ModelAndView showSearch() {
    return new ModelAndView(ViewNames.Frontend.SEARCH);
  }

  @GetMapping(path = "/{discogsId}")
  public ModelAndView showArtistDetails(@PathVariable String discogsId) {
    return new ModelAndView(ViewNames.Frontend.ARTIST_DETAILS);
  }

}
