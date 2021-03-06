package rocks.metaldetector.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;

public interface ImageBySizeFetcher {

  Map<ImageSize, String> getImages();

  @ArtifactForFramework
  @JsonProperty(access = READ_ONLY)
  default String getThumbnailImage() {
    return getImageBySizePriority(List.of(XS, S));
  }

  @ArtifactForFramework
  @JsonProperty(access = READ_ONLY)
  default String getSmallImage() {
    return getImageBySizePriority(List.of(S, M, XS));
  }

  @ArtifactForFramework
  @JsonProperty(access = READ_ONLY)
  default String getMediumImage() {
    return getImageBySizePriority(List.of(M, L, S));
  }

  @ArtifactForFramework
  @JsonProperty(access = READ_ONLY)
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
