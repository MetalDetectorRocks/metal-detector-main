package rocks.metaldetector.web.transformer;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import rocks.metaldetector.support.Sorting;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rocks.metaldetector.support.Sorting.Direction.ASC;
import static rocks.metaldetector.support.Sorting.Direction.DESC;

@Component
public class SortingTransformer {

  public Sorting transform(Sort sort) {
    return new Sorting(transformOrders(sort.get()));
  }

  private List<Sorting.Order> transformOrders(Stream<Sort.Order> orders) {
    return orders.map(order -> new Sorting.Order(transformDirection(order.getDirection()), order.getProperty()))
        .collect(Collectors.toList());
  }

  private Sorting.Direction transformDirection(Sort.Direction direction) {
    if (direction == Sort.Direction.ASC) {
      return ASC;
    }
    return DESC;
  }
}
