package rocks.metaldetector.butler.facade;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.util.List;

public interface ReleaseService {

  List<ReleaseDto> findReleases(Iterable<String> artists, LocalDate dateFrom, LocalDate dateTo);

}
