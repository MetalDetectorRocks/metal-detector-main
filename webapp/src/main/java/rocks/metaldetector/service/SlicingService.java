package rocks.metaldetector.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlicingService {

  public <T> List<T> slice(List<T> list, int page, int size) {
    page = Math.max(page, 1);
    int start = (page - 1) * size;
    int end = Math.min(start + size, list.size());

    return list.subList(start, end);
  }
}
