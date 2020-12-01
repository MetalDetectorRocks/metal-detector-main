package rocks.metaldetector.web.transformer;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import rocks.metaldetector.support.DetectorSort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rocks.metaldetector.support.DetectorSort.Direction.ASC;
import static rocks.metaldetector.support.DetectorSort.Direction.DESC;

@Component
public class DetectorSortTransformer {

  public DetectorSort transform(Sort sort) {
    return new DetectorSort(transformOrders(sort.get()));
  }

  private List<DetectorSort.Order> transformOrders(Stream<Sort.Order> orders) {
    return orders.map(order -> new DetectorSort.Order(transformDirection(order.getDirection()), order.getProperty()))
        .collect(Collectors.toList());
  }

  private DetectorSort.Direction transformDirection(Sort.Direction direction) {
    return direction == Sort.Direction.ASC ? ASC : DESC;
  }
}
