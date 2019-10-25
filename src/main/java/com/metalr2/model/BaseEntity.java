package com.metalr2.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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

}
