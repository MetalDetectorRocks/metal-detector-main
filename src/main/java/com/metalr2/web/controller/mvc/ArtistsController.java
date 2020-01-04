package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.dto.request.SearchRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(Endpoints.Frontend.ARTISTS)
public class ArtistsController {

  @GetMapping(path = "/search")
  public ModelAndView showSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size) {
    Map<String,Object> viewModel = new HashMap<>();
    viewModel.put("searchRequest", new SearchRequest(query,page,size));
    return new ModelAndView(ViewNames.Frontend.SEARCH, viewModel);
  }

}
