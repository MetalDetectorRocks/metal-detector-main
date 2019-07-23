package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.demo.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.search.ArtistSearchResult;
import com.metalr2.web.dto.discogs.search.ArtistSearchResults;
import com.metalr2.web.dto.request.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
public class SearchController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private static final String DEFAULT_PAGE_SIZE = "25";
  private static final String DEFAULT_PAGE = "1";

  @Autowired
  public SearchController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
  }

  @ModelAttribute
  private SearchRequest searchRequest() {
    return new SearchRequest();
  }

  @ModelAttribute List<ArtistSearchResult> searchResults(){
    return new ArrayList<>();
  }

  @PostMapping({Endpoints.SEARCH})
  public ModelAndView handleSearchRequest(@ModelAttribute SearchRequest searchRequest) {
    log.info(searchRequest.getArtistName());

    Optional<ArtistSearchResults> artistSearchResultsOptional = artistSearchRestClient.searchForArtist(searchRequest.getArtistName(), DEFAULT_PAGE, DEFAULT_PAGE_SIZE);

    if (artistSearchResultsOptional.isEmpty()){
      return new ModelAndView(ViewNames.SEARCH);
    }

    ArtistSearchResults artistSearchResults = artistSearchResultsOptional.get();

    Page<ArtistSearchResult> paginationPage = findPaginated(PageRequest.of(0, 10), artistSearchResults.getResults());
    Map<String,Object> viewModel = buildResultMap(paginationPage, artistSearchResults.getResults());
    viewModel.put("artistName", searchRequest.getArtistName());

    return new ModelAndView(ViewNames.SEARCH, viewModel);
  }

  private Map<String,Object> buildResultMap(Page<ArtistSearchResult> artistSearchResultsPage, List<ArtistSearchResult> artistSearchResults) {
    Map<String, Object> map = new HashMap<>();
    map.put("artistSearchResults", artistSearchResults);
    map.put("artistSearchResultsPage", artistSearchResultsPage);

    int totalPages = artistSearchResultsPage.getTotalPages();
    if (totalPages > 0) {
      List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
      map.put("pageNumbers", pageNumbers);
    }
    return map;
  }

  @GetMapping({Endpoints.SEARCH})
  public ModelAndView showSearch(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @ModelAttribute List<ArtistSearchResult> searchResults) {
    if (searchResults.isEmpty()){
      return new ModelAndView(ViewNames.SEARCH);
    }
    Page<ArtistSearchResult> paginationPage = findPaginated(PageRequest.of(0, 10), searchResults);
    Map<String,Object> viewModel = buildResultMap(paginationPage, searchResults);

    return new ModelAndView(ViewNames.SEARCH, viewModel);
  }

  private Page<ArtistSearchResult> findPaginated(Pageable pageable, List<ArtistSearchResult> searchResults) {
    int pageSize = pageable.getPageSize();
    int currentPage = pageable.getPageNumber();
    int startItem = currentPage * pageSize;
    List<ArtistSearchResult> list;

    if (searchResults.size() < startItem) {
      list = Collections.emptyList();
    } else {
      int toIndex = Math.min(startItem + pageSize, searchResults.size());
      list = searchResults.subList(startItem, toIndex);
    }

    return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), searchResults.size());
  }

}
