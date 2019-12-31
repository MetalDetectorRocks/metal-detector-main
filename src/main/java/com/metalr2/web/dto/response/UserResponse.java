package com.metalr2.web.dto.response;

import com.metalr2.model.ArtifactForFramework;
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

  // ToDo DanielW: Die getAsHtml-Methoden können ausgebaut werden, sobald das Cell Rendering in JS stattfindet
  @ArtifactForFramework // called in html
  public String getStatusAsHtml() {
    return enabled ? "<span class=\"badge badge-success\">Enabled</span>" :
                     "<span class=\"badge badge-secondary\">Disabled</span>";
  }

  @ArtifactForFramework // called in html
  public String getRoleAsHtml() {
    return role.equals("Administrator") ? "<span class=\"badge badge-danger\">" + role + "</span>" :
                                          "<span class=\"badge badge-info\">" + role + "</span>";
  }

  @ArtifactForFramework // called in html
  public String getActionsAsHtml() {
    return  "<a href=\"#\"><i class=\"material-icons table-action-icon\">edit</i></a>" +
            "<a href=\"#\"><i class=\"material-icons table-action-icon\">delete</i></a>";
  }

}
