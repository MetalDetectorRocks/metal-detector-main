package rocks.metaldetector.service.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private String publicId;
  private String username;
  private String email;
  private String avatar;
  private String plainPassword;
  private boolean enabled;
  private String role;
  private LocalDateTime lastLogin;
  private String createdBy;
  private LocalDateTime createdDateTime;
  private LocalDateTime lastModifiedDateTime;
  private String lastModifiedBy;

}
