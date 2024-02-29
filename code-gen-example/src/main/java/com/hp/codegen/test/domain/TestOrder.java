package com.hp.codegen.test.domain;


import com.hp.codegen.processor.api.GenFeign;
import com.hp.codegen.processor.command.create.GenCreateCommand;
import com.hp.codegen.processor.command.update.GenUpdateCommand;
import com.hp.codegen.processor.context.create.GenCreateContext;
import com.hp.codegen.processor.context.update.GenUpdateContext;
import com.hp.codegen.processor.controller.GenController;
import com.hp.codegen.processor.event.GenEvent;
import com.hp.codegen.processor.event.GenEventListener;
import com.hp.codegen.processor.mapper.GenMapper;
import com.hp.codegen.processor.repository.GenRepository;
import com.hp.codegen.processor.request.GenCreateRequest;
import com.hp.codegen.processor.request.GenPageRequest;
import com.hp.codegen.processor.request.GenRequest;
import com.hp.codegen.processor.request.GenUpdateRequest;
import com.hp.codegen.processor.response.GenPageResponse;
import com.hp.codegen.processor.response.GenResponse;
import com.hp.codegen.processor.service.GenService;
import com.hp.codegen.processor.service.GenServiceImpl;
import com.hp.codegen.test.domain.command.CreateTestOrderCommand;
import com.hp.codegen.test.domain.context.CreateTestOrderContext;
import com.hp.codegen.test.domain.context.UpdateTestOrderContext;
import com.hp.codegen.test.domain.mapper.TestOrderMapper;
import com.hp.common.base.annotations.FieldDesc;
import com.hp.common.base.enums.ValidStatus;
import com.hp.jpa.BaseJpaAggregate;
import com.hp.jpa.convertor.ValidStatusConverter;
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
 @GenFeign(pkgName = "com.hp.codegen.test.api.order.service", serverName="orderServer")
 @GenRequest(pkgName = "com.hp.codegen.test.api.order.request")
 @GenCreateRequest(pkgName = "com.hp.codegen.test.api.order.request")
 @GenUpdateRequest(pkgName = "com.hp.codegen.test.api.order.request")
 @GenPageRequest(pkgName = "com.hp.codegen.test.api.order.request")
 @GenResponse(pkgName = "com.hp.codegen.test.api.order.response")
 @GenPageResponse(pkgName = "com.hp.codegen.test.api.order.response")
 @GenEvent(pkgName = "com.hp.codegen.test.domain.events")
 @GenEventListener(pkgName = "com.hp.codegen.test.domain.events")
 @GenCreateContext(pkgName = "com.hp.codegen.test.domain.context")
 @GenUpdateContext(pkgName = "com.hp.codegen.test.domain.context")
 @GenCreateCommand(pkgName = "com.hp.codegen.test.domain.command")
 @GenUpdateCommand(pkgName = "com.hp.codegen.test.domain.command")
 @GenController(pkgName = "com.hp.codegen.test.controller")
 @GenService(pkgName = "com.hp.codegen.test.domain.service")
 @GenServiceImpl(pkgName = "com.hp.codegen.test.domain.service.impl")
 @GenRepository(pkgName = "com.hp.codegen.test.domain.repository")
 @GenMapper(pkgName = "com.hp.codegen.test.domain.mapper")
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

     public void enable(){
         setStatus(ValidStatus.VALID);
     }

     public void disable(){
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
