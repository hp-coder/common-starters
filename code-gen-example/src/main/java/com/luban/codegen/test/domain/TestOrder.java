package com.luban.codegen.test.domain;

import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.enums.ValidStatus;
import com.luban.jpa.BaseJpaAggregate;
import com.luban.jpa.convertor.ValidStatusConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Optional;

/**
 * @author hp
 */


@GenService(pkgName = "com.luban.codegen.test.domain.service")
@GenServiceImpl(pkgName = "com.luban.codegen.test.domain.service.impl")
@GenRepository(pkgName = "com.luban.codegen.test.domain.repository")
@GenMapper(pkgName = "com.luban.codegen.test.domain.mapper")
@Entity
@Table(name = "test_order")
@EqualsAndHashCode(callSuper = true)
@Data
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
}
