package com.hp.jpa;

import com.hp.jpa.converter.InstantLongConverter;
import com.hp.jpa.id.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;

@MappedSuperclass
@Getter
public abstract class BaseJpaAggregate extends AbstractAggregateRoot<BaseJpaAggregate> {

    @Id
    @GeneratedValue(generator = "customIdGenerator")
    @GenericGenerator(name = "customIdGenerator", type = CustomIdGenerator.class)
    @Setter
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", updatable = false)
    @Convert(converter = InstantLongConverter.class)
    @Setter(AccessLevel.PROTECTED)
    private Instant createdAt;

    @Column(name = "updated_at")
    @Convert(converter = InstantLongConverter.class)
    @Setter(AccessLevel.PROTECTED)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    @Setter(AccessLevel.PRIVATE)
    private int version;

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
