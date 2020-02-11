package rocks.metaldetector.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponse {
	
  private String publicId;
  private String username;
  private String email;
  private boolean enabled;
  private String role;
  private LocalDateTime lastLogin;
  private String createdBy;
  private LocalDateTime createdDateTime;
  private LocalDateTime lastModifiedDateTime;
  private String lastModifiedBy;

}
