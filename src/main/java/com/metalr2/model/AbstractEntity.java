package com.metalr2.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Getter
@EqualsAndHashCode(of = "id")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_date", updatable = false)
  @ArtifactForFramework
  private Date createdDate;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  @ArtifactForFramework
  private String createdBy;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modified_date")
  @ArtifactForFramework
  private Date lastModifiedDate;

  @LastModifiedBy
  @Column(name = "last_modified_by")
  @ArtifactForFramework
  private String lastModifiedBy;

  public boolean isNew() {
    return id == null;
  }

  public LocalDate getCreatedDate() {
    return createdDate != null ? createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
  }

  public LocalDate getLastModifiedDate() {
    return lastModifiedDate != null ? lastModifiedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
  }

}
