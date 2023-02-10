package com.luban.jpa;

import com.luban.jpa.convertor.InstantLongConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
@Data
public abstract class BaseJpaAggregate extends AbstractAggregateRoot<BaseJpaAggregate> {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(name = "snowflakeIdGenerator", strategy = "com.luban.jpa.id.SnowflakeIdGenerator")
    @Setter(AccessLevel.PROTECTED)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", nullable = true, updatable = false)
    @Convert(converter = InstantLongConverter.class)
    @Setter(AccessLevel.PROTECTED)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = true)
    @Convert(converter = InstantLongConverter.class)
    @Setter(AccessLevel.PROTECTED)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    @Setter(AccessLevel.PRIVATE)
    private Integer version;

    @PrePersist
    public void prePersist(){
        this.setCreatedAt(Instant.now());
        this.setUpdatedAt(Instant.now());
    }

    @PreUpdate
    public void preUpdate(){
        this.setUpdatedAt(Instant.now());
    }
}
