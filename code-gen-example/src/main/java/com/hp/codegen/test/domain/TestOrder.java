package com.hp.codegen.test.domain;

import com.hp.codegen.processor.controller.GenController;
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
import com.hp.jpa.convertor.LocalDateTimeConverter;
import com.hp.jpa.convertor.ValidStatusConverter;
import lombok.Data;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HP
 * @date 2022/10/24
 */
@GenResponse(pkgName = "com.hp.codegen.test.domain.response")
@GenVo(pkgName = "com.hp.codegen.test.domain.response")
@GenDto(pkgName = "com.hp.codegen.test.domain.request")
@GenRequest(pkgName = "com.hp.codegen.test.domain.request")
@GenController(pkgName = "com.hp.codegen.test.domain.controller")
@GenService(pkgName = "com.hp.codegen.test.domain.service")
@GenServiceImpl(pkgName = "com.hp.codegen.test.domain.service.impl")
@GenRepository(pkgName = "com.hp.codegen.test.domain.repository")
@GenMapper(pkgName = "com.hp.codegen.test.domain.mapper")
@Entity
@Table
@Data
public class TestOrder extends BaseJpaAggregate implements Serializable {

    @Convert(converter = ValidStatusConverter.class)
    @FieldDesc("状态")
    private ValidStatus validStatus;

    @Convert
    @FieldDesc("没有实际converter")
    private ValidStatus noConverter;

    @Convert(converter = LocalDateTimeConverter.class)
    @FieldDesc("时间")
    private LocalDateTime date;

    private Long testLong;

    public void init() {
    }

    public void valid() {
    }

    public void invalid() {
    }
}
