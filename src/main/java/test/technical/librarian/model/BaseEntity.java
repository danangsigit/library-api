package test.technical.librarian.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -4209583240063664137L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @Column(name = "created_date", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    @Column(name = "last_updated_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastUpdatedDate;

    @Column(name = "created_by", length=64, nullable=false)
    @CreatedBy
    private String createdBy;

    @Column(name = "last_updated_by", length=64)
    @LastModifiedBy
    private String lastUpdatedBy;

    @Column(name = "is_active", nullable = false)
    private boolean active = Boolean.TRUE;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = Boolean.FALSE;

    @PrePersist
    public void prePersist() {
        lastUpdatedDate = createdDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedDate = new Date();
    }

    public BaseEntity(String id) {
        this.id = id;
    }

}