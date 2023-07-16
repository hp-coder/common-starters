package com.luban.codegen.test.domain;

import com.luban.codegen.processor.controller.GenController;
import com.luban.codegen.processor.dto.GenDto;
import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.request.GenRequest;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.processor.vo.GenVo;
import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.enums.ValidStatus;
import com.luban.jpa.BaseJpaAggregate;
import com.luban.jpa.convertor.LocalDateTimeConverter;
import com.luban.jpa.convertor.ValidStatusConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HP
 * @date 2022/10/24
 */
@GenResponse(pkgName = "com.luban.codegen.test.domain.response")
@GenVo(pkgName = "com.luban.codegen.test.domain.response")
@GenDto(pkgName = "com.luban.codegen.test.domain.request")
@GenRequest(pkgName = "com.luban.codegen.test.domain.request")
@GenController(pkgName = "com.luban.codegen.test.domain.controller")
@GenService(pkgName = "com.luban.codegen.test.domain.service")
@GenServiceImpl(pkgName = "com.luban.codegen.test.domain.service.impl")
@GenRepository(pkgName = "com.luban.codegen.test.domain.repository")
@GenMapper(pkgName = "com.luban.codegen.test.domain.mapper")
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
