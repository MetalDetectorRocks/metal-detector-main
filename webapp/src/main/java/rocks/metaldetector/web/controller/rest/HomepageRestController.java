package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.home.HomepageService;
import rocks.metaldetector.web.api.response.HomepageResponse;

@RestController
@RequestMapping(Endpoints.Rest.HOME)
@AllArgsConstructor
public class HomepageRestController {

  private final HomepageService homepageService;

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<HomepageResponse> handleHomeRequest() {
    HomepageResponse homepageResponse = homepageService.createHomeResponse();
    return ResponseEntity.ok(homepageResponse);
  }
}
