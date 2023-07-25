package com.luban.codegen.test.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
import com.luban.jpa.convertor.LocalDateTimeConverter;
import com.luban.mybatisplus.BaseMbpAggregate;
import com.luban.mybatisplus.convertor.LocalDateTimeTypeConverter;
import com.luban.mybatisplus.convertor.ValidStatusConverter;
import lombok.Data;

import javax.persistence.Convert;
import java.time.LocalDateTime;

/**
 * @author hp 2023/4/10
 */
@GenRequest(pkgName = "com.luban.codegen.test.domain.request")
@GenResponse(pkgName = "com.luban.codegen.test.domain.response")
@GenDto(pkgName = "com.luban.codegen.test.domain.request")
@GenVo(pkgName = "com.luban.codegen.test.domain.response")
@GenController(pkgName = "com.luban.codegen.test.controller")
@GenService(pkgName = "com.luban.codegen.test.domain.service")
@GenServiceImpl(pkgName = "com.luban.codegen.test.domain.service.impl")
@GenRepository(pkgName = "com.luban.codegen.test.domain.repository")
@GenMapper(pkgName = "com.luban.codegen.test.domain.mapper")
@TableName(value="test_order")
@Data
public class TestOrder extends BaseMbpAggregate {

    @TableField(typeHandler = ValidStatusConverter.class)
    @FieldDesc("状态")
    private ValidStatus status;

    @Convert(converter = com.luban.jpa.convertor.ValidStatusConverter.class)
    @FieldDesc("状态")
    private ValidStatus validStatus;

    @Convert
    @FieldDesc("没有实际converter")
    private ValidStatus noConverter;

    @TableField(typeHandler = LocalDateTimeTypeConverter.class)
    @Convert(converter = LocalDateTimeConverter.class)
    @FieldDesc("时间")
    private LocalDateTime date;

    private Long testLong;

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
