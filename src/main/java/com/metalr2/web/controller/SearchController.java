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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
public class SearchController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private List<SearchResponse> searchResults = Collections.emptyList();
  private String searchedArtist = "";

  @Autowired
  public SearchController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
  }

  @ModelAttribute
  private SearchRequest searchRequest() {
    return new SearchRequest();
  }

  @GetMapping({Endpoints.SEARCH})
  public ModelAndView showSearchForm() {
    return new ModelAndView(ViewNames.SEARCH);
  }

  @PostMapping({Endpoints.SEARCH})
  public ModelAndView handleSearchRequest(@ModelAttribute SearchRequest searchRequest) {

    log.info(searchRequest.getArtistName());

    searchedArtist = searchRequest.getArtistName();
    searchResults = artistSearchRestClient.searchForArtist(searchRequest.getArtistName())
            .stream().map(result -> new SearchResponse(result.getId(), result.getTitle(), "https://discogs.com" + result.getUri()))
            .collect(Collectors.toList());

    return showSearchResult(0, 0);

  }

  @GetMapping({Endpoints.SEARCH_RESULT})
  public ModelAndView showSearchResult(@RequestParam("page") int page, @RequestParam("size") int size) {

    int currentPage = page == 0 ? 1 : page;
    int pageSize = size == 0 ? 10 : size;

    Page<SearchResponse> searchResultsPage = findPaginated(PageRequest.of(currentPage - 1, pageSize), searchResults);

    Map<String, Object> map = new HashMap<>();
    map.put("artistName", searchedArtist);
    map.put("searchResults", searchResults);
    map.put("searchResultsPage", searchResultsPage);

    int totalPages = searchResultsPage.getTotalPages();
    if (totalPages > 0) {
      List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
      map.put("pageNumbers", pageNumbers);
    }

    return new ModelAndView(ViewNames.SEARCH_RESULT, map);

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
