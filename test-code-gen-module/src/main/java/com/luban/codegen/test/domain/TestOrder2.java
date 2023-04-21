package com.luban.codegen.test.domain;

import com.luban.codegen.processor.Ignore;
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
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

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
public class TestOrder2 extends BaseJpaAggregate implements Serializable {

    @Ignore
    @FieldDesc("计量单位")
    private String measureUnit;

    @FieldDesc("状态")
    private ValidStatus validStatus;

    public void init(){}
    public void valid(){}
    public void invalid(){}
}
