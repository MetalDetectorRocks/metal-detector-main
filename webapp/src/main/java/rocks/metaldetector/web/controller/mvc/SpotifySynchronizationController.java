package rocks.metaldetector.web.controller.mvc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;

@Controller
@AllArgsConstructor
@RequestMapping(Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION)
public class SpotifySynchronizationController {

  private final SpotifyUserAuthorizationService spotifyUserAuthorizationService;

  @GetMapping
  public ModelAndView showSettings() {
    return new ModelAndView(ViewNames.Frontend.SPOTIFY_SYNCHRONIZATION);
  }

  @GetMapping(path = Endpoints.Frontend.SPOTIFY_CALLBACK)
  public ModelAndView handleSpotifyCallback(@RequestParam(value = "code") String code,
                                            @RequestParam(value = "state") String state) {
    spotifyUserAuthorizationService.fetchInitialToken(state, code);
    return new ModelAndView(ViewNames.Frontend.SPOTIFY_SYNCHRONIZATION);
  }
}
