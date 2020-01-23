package com.metalr2.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ReleasesRequest {

  @Nullable
  private LocalDate dateFrom;

  @Nullable
  private LocalDate dateTo;

  @NotNull
  private List<String> artists;

}
