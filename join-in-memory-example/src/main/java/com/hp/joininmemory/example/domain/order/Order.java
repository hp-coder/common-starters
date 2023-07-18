package com.hp.joininmemory.example.domain.order;

import com.hp.codegen.processor.dto.GenDto;
import com.hp.codegen.processor.mapper.GenMapper;
import com.hp.codegen.processor.repository.GenRepository;
import com.hp.codegen.processor.request.GenRequest;
import com.hp.codegen.processor.response.GenResponse;
import com.hp.codegen.processor.service.GenService;
import com.hp.codegen.processor.service.GenServiceImpl;
import com.hp.codegen.processor.vo.GenVo;
import com.hp.common.base.annotations.FieldDesc;
import com.hp.common.base.enums.ValidStatus;
import com.hp.jpa.BaseJpaAggregate;
import com.hp.jpa.convertor.ValidStatusConverter;
import lombok.Data;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author hp 2023/4/10
 */
@GenRequest(pkgName = "com.hp.joininmemory.example.domain.order.request")
@GenResponse(pkgName = "com.hp.joininmemory.example.domain.order.response")
@GenDto(pkgName = "com.hp.joininmemory.example.domain.order.request")
@GenVo(pkgName = "com.hp.joininmemory.example.domain.order.response")
@GenService(pkgName = "com.hp.joininmemory.example.domain.order.service")
@GenServiceImpl(pkgName = "com.hp.joininmemory.example.domain.order.service.impl")
@GenRepository(pkgName = "com.hp.joininmemory.example.domain.order.repository")
@GenMapper(pkgName = "com.hp.joininmemory.example.domain.order.mapper")
@Entity
@Table(name = "t_order")
@Data
public class Order extends BaseJpaAggregate {

    //.... other fields

    @FieldDesc("订单号")
    private String orderNo;

    @Convert(converter = ValidStatusConverter.class)
    @FieldDesc("状态")
    private ValidStatus status;

    public void init(){
        setStatus(ValidStatus.VALID);
    }

    public void valid(){
        setStatus(ValidStatus.VALID);
    }

    public void invalid(){
        setStatus(ValidStatus.INVALID);
    }
}
