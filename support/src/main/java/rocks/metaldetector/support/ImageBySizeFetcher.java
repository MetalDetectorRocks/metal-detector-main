package rocks.metaldetector.support;

import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;

public interface ImageBySizeFetcher {

  Map<ImageSize, String> getImages();

  @ArtifactForFramework
  default String getThumbnailImage() {
    return getImageBySizePriority(List.of(XS, S));
  }

  @ArtifactForFramework
  default String getSmallImage() {
    return getImageBySizePriority(List.of(S, M, XS));
  }

  @ArtifactForFramework
  default String getMediumImage() {
    return getImageBySizePriority(List.of(M, S, L));
  }

  @ArtifactForFramework
  default String getLargeImage() {
    return getImageBySizePriority(List.of(L, M));
  }

  private String getImageBySizePriority(List<ImageSize> imageSizesPriorities) {
    return imageSizesPriorities.stream()
            .map(size -> getImages().get(size))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
  }
}
