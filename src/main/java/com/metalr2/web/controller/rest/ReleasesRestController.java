package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import com.metalr2.service.releases.ReleasesService;
import com.metalr2.web.dto.releases.ReleasesButlerRequest;
import com.metalr2.web.dto.request.ReleasesRequest;
import com.metalr2.web.dto.response.ReleasesResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Endpoints.Rest.RELEASES)
public class ReleasesRestController {

  private final ReleasesService releasesService;
  private final ModelMapper mapper;

  @Autowired
  public ReleasesRestController(ReleasesService releasesService, ModelMapper mapper) {
    this.releasesService = releasesService;
    this.mapper = mapper;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<ReleasesResponse>> getReleases(@Valid @RequestBody ReleasesRequest request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }

    List<ReleasesResponse> releaseDtos = releasesService.getReleases(mapper.map(request, ReleasesButlerRequest.class)).stream()
        .map(releaseDto -> mapper.map(releaseDto, ReleasesResponse.class))
        .collect(Collectors.toList());
    return ResponseEntity.ok(releaseDtos);
  }
}
