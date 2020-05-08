package rocks.metaldetector.service.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.service.artist.ArtistDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private String publicId;
  private String username;
  private String email;
  private String plainPassword;
  private boolean enabled;
  private String role;
  private LocalDateTime lastLogin;
  private String createdBy;
  private LocalDateTime createdDateTime;
  private LocalDateTime lastModifiedDateTime;
  private String lastModifiedBy;
  private List<ArtistDto> followedArtists;

}
