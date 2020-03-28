package rocks.metaldetector.butler.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ReleaseDto {

  private String artist;
  private List<String> additionalArtists;
  private String albumTitle;
  private LocalDate releaseDate;
  private String estimatedReleaseDate;

}
