package rocks.metaldetector.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sorting {

  private List<Order> orders;

  public Sorting(Direction direction, List<String> properties) {
    this.orders = properties.stream()
        .map(property -> new Order(direction, property))
        .collect(Collectors.toList());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Order {

    private Direction direction;
    private String property;

  }

  public enum Direction {
    ASC,
    DESC
  }

  @Override
  public String toString() {
    return orders.stream()
        .map(order -> "sort=" + order.getProperty() + "," + order.getDirection())
        .collect(Collectors.joining("&"));
  }
}
