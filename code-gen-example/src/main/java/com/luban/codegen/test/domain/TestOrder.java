package com.luban.codegen.test.domain;

import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.request.GenRequest;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.enums.ValidStatus;
import com.luban.jpa.BaseJpaAggregate;
import com.luban.jpa.convertor.ValidStatusConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */


@GenRequest(pkgName =  "com.luban.codegen.test.domain.request")
@GenService(pkgName = "com.luban.codegen.test.domain.service")
@GenServiceImpl(pkgName = "com.luban.codegen.test.domain.service.impl")
@GenRepository(pkgName = "com.luban.codegen.test.domain.repository")
@GenMapper(pkgName = "com.luban.codegen.test.domain.mapper")
@Entity
@Table(name = "test_order")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TestOrder extends BaseJpaAggregate {

    @Convert(converter = ValidStatusConverter.class)
    @FieldDesc("状态")
    private ValidStatus status;

//    public static TestOrder createTestOrder(CreateTestOrderContext context) {
//        final TestOrder entity = new TestOrder();
//        context.setEntity(entity);
//        return entity;
//    }
//
//    public void updateTestOrder(UpdateTestOrderContext context) {
//        context.setEntity(this);
//    }

    public boolean enabled() {
        return Optional.ofNullable(getStatus())
                .map(ValidStatus::valid)
                .orElse(false);
    }

    public boolean disabled() {
        return !enabled();
    }

    public void init() {
        setStatus(ValidStatus.VALID);
    }

    public void valid() {
        setStatus(ValidStatus.VALID);
    }

    public void invalid() {
        setStatus(ValidStatus.INVALID);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TestOrder testOrder = (TestOrder) o;
        return getId() != null && Objects.equals(getId(), testOrder.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
