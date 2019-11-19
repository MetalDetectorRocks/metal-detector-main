package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {

  private int totalPages;
  private int currentPage;
  private int size;
  private int nextPage;

}