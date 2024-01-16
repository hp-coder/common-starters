package com.luban.codegen.test.domain;


import com.luban.codegen.processor.api.GenFeign;
import com.luban.codegen.processor.command.create.GenCreateCommand;
import com.luban.codegen.processor.command.update.GenUpdateCommand;
import com.luban.codegen.processor.context.create.GenCreateContext;
import com.luban.codegen.processor.context.update.GenUpdateContext;
import com.luban.codegen.processor.controller.GenController;
import com.luban.codegen.processor.event.GenEvent;
import com.luban.codegen.processor.event.GenEventListener;
import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.request.GenCreateRequest;
import com.luban.codegen.processor.request.GenPageRequest;
import com.luban.codegen.processor.request.GenRequest;
import com.luban.codegen.processor.request.GenUpdateRequest;
import com.luban.codegen.processor.response.GenPageResponse;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.test.domain.command.CreateTestOrderCommand;
import com.luban.codegen.test.domain.context.CreateTestOrderContext;
import com.luban.codegen.test.domain.context.UpdateTestOrderContext;
import com.luban.codegen.test.domain.mapper.TestOrderMapper;
import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.enums.ValidStatus;
import com.luban.jpa.BaseJpaAggregate;
import com.luban.jpa.convertor.ValidStatusConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

/**
  * @author hp
  */
 @GenFeign(pkgName = "com.luban.codegen.test.api.order.service", serverName="orderServer")
 @GenRequest(pkgName = "com.luban.codegen.test.api.order.request")
 @GenCreateRequest(pkgName = "com.luban.codegen.test.api.order.request")
 @GenUpdateRequest(pkgName = "com.luban.codegen.test.api.order.request")
 @GenPageRequest(pkgName = "com.luban.codegen.test.api.order.request")
 @GenResponse(pkgName = "com.luban.codegen.test.api.order.response")
 @GenPageResponse(pkgName = "com.luban.codegen.test.api.order.response")
 @GenEvent(pkgName = "com.luban.codegen.test.domain.events")
 @GenEventListener(pkgName = "com.luban.codegen.test.domain.events")
 @GenCreateContext(pkgName = "com.luban.codegen.test.domain.context")
 @GenUpdateContext(pkgName = "com.luban.codegen.test.domain.context")
 @GenCreateCommand(pkgName = "com.luban.codegen.test.domain.command")
 @GenUpdateCommand(pkgName = "com.luban.codegen.test.domain.command")
 @GenController(pkgName = "com.luban.codegen.test.controller")
 @GenService(pkgName = "com.luban.codegen.test.domain.service")
 @GenServiceImpl(pkgName = "com.luban.codegen.test.domain.service.impl")
 @GenRepository(pkgName = "com.luban.codegen.test.domain.repository")
 @GenMapper(pkgName = "com.luban.codegen.test.domain.mapper")
 @Entity
 @Table(name = "test_order")
 @Getter
 @Setter
 public class TestOrder extends BaseJpaAggregate {



     @Convert(converter = ValidStatusConverter.class)
     @FieldDesc("状态")
     private ValidStatus status;

     public static TestOrder createTestOrder(CreateTestOrderContext context){
         final CreateTestOrderCommand command = context.getCommand();
         final TestOrder entity = TestOrderMapper.INSTANCE.createCommandToEntity(command);
         context.setEntity(entity);
         return entity;
     }

     public void updateTestOrder(UpdateTestOrderContext context){
         context.setEntity(this);
         context.getCommand().updateTestOrder(this);
     }

     public boolean enabled() {
         return Optional.ofNullable(getStatus())
                 .map(ValidStatus::valid)
                 .orElse(false);
     }

     public boolean disabled() {
         return !enabled();
     }

     public void init(){
         setStatus(ValidStatus.VALID);
     }

     public void valid(){
         setStatus(ValidStatus.VALID);
     }

     public void invalid(){
         setStatus(ValidStatus.INVALID);
     }

     @Override
     public final boolean equals(Object o) {
         if (this == o) {
             return true;
         }
         if (o == null) {
             return false;
         }
         Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
         Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
         if (thisEffectiveClass != oEffectiveClass) {
             return false;
         }
         TestOrder that = (TestOrder) o;
         return getId() != null && Objects.equals(getId(), that.getId());
     }

     @Override
     public final int hashCode() {
         return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
     }
 }
