package rocks.metaldetector.persistence.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import rocks.metaldetector.support.ArtifactForFramework;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@EqualsAndHashCode(of = "id")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_date", updatable = false)
  @ArtifactForFramework
  private Date createdDateTime;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  @ArtifactForFramework
  private String createdBy;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modified_date")
  @ArtifactForFramework
  private Date lastModifiedDateTime;

  @LastModifiedBy
  @Column(name = "last_modified_by")
  @ArtifactForFramework
  private String lastModifiedBy;

  public boolean isNew() {
    return id == null;
  }

  public LocalDateTime getCreatedDateTime() {
    return createdDateTime != null ? createdDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
  }

  public LocalDateTime getLastModifiedDateTime() {
    return lastModifiedDateTime != null ? lastModifiedDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
  }

  public void setCreatedDateTime(Date newCreatedDateTime) {
    this.createdDateTime = checkInitialValueAssignment(this.createdDateTime, newCreatedDateTime);
  }

  public void setCreatedBy(String newCreatedBy) {
    this.createdBy = checkInitialValueAssignment(this.createdBy, newCreatedBy);
  }

  public void setLastModifiedDateTime(Date newLastModifiedDateTime) {
    this.lastModifiedDateTime = checkInitialValueAssignment(this.lastModifiedDateTime, newLastModifiedDateTime);
  }

  public void setLastModifiedBy(String newLastModifiedBy) {
    this.lastModifiedBy = checkInitialValueAssignment(this.lastModifiedBy, newLastModifiedBy);
  }

  /*
   * At runtime the value is automatically set by Spring. This method allows you to set a value
   * if the class is used in a unit test, for example.
   */
  protected  <T> T checkInitialValueAssignment(T oldValue, T newValue) {
    if (oldValue != null) {
      throw new UnsupportedOperationException("It's not allowed to reset the value!");
    }
    else {
      return newValue;
    }
  }

}
