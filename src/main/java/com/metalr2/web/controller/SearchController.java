package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.demo.ArtistSearchRestClient;
import com.metalr2.web.dto.request.SearchRequest;
import com.metalr2.web.dto.response.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
@SessionAttributes("searchResults")
public class SearchController {

  private final ArtistSearchRestClient artistSearchRestClient;

  @Autowired
  public SearchController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
  }

  @ModelAttribute
  private SearchRequest searchRequest() {
    return new SearchRequest();
  }

  @ModelAttribute
  private List<SearchResponse> searchResults(){
    return new ArrayList<>();
  }

  @PostMapping({Endpoints.SEARCH})
  public ModelAndView handleSearchRequest(@ModelAttribute SearchRequest searchRequest, @ModelAttribute List<SearchResponse> searchResults) {
    log.info(searchRequest.getArtistName());

    searchResults.addAll( artistSearchRestClient.searchForArtist(searchRequest.getArtistName())
            .stream().map(result -> new SearchResponse(result.getId(), result.getTitle(), "https://discogs.com" + result.getUri()))
            .collect(Collectors.toList())); // TODO nils: 22.07.19 namen verbessern: searchedArtist, searchResults; auch andere methoden checken

    Page<SearchResponse> paginationPage = findPaginated(PageRequest.of(0, 10), searchResults);
    Map<String,Object> viewModel = buildResultMap(paginationPage, searchResults);
    viewModel.put("artistName", searchRequest.getArtistName());

    return new ModelAndView(ViewNames.SEARCH, viewModel);
  }

  private Map<String,Object> buildResultMap(Page<SearchResponse> searchResultsPage, List<SearchResponse> searchResults) {
    Map<String, Object> map = new HashMap<>();
    map.put("searchResults", searchResults);
    map.put("searchResultsPage", searchResultsPage);

    int totalPages = searchResultsPage.getTotalPages();
    if (totalPages > 0) {
      List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
      map.put("pageNumbers", pageNumbers);
    }
    return map;
  }

  @GetMapping({Endpoints.SEARCH})
  public ModelAndView showSearch(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @ModelAttribute List<SearchResponse> searchResults) {
    if (searchResults.isEmpty()){
      return new ModelAndView(ViewNames.SEARCH);
    }
    Page<SearchResponse> paginationPage = findPaginated(PageRequest.of(0, 10), searchResults);
    Map<String,Object> viewModel = buildResultMap(paginationPage, searchResults);

    return new ModelAndView(ViewNames.SEARCH, viewModel);
  }

  private Page<SearchResponse> findPaginated(Pageable pageable, List<SearchResponse> searchResults) {
    int pageSize = pageable.getPageSize();
    int currentPage = pageable.getPageNumber();
    int startItem = currentPage * pageSize;
    List<SearchResponse> list;

    if (searchResults.size() < startItem) {
      list = Collections.emptyList();
    } else {
      int toIndex = Math.min(startItem + pageSize, searchResults.size());
      list = searchResults.subList(startItem, toIndex);
    }

    return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), searchResults.size());
  }

}
