// --- Auto Generated By CodeGen Module ---
package com.luban.codegen.test.domain.mapper;

import com.luban.codegen.test.api.order.request.TestOrderCreateRequest;
import com.luban.codegen.test.api.order.request.TestOrderUpdateRequest;
import com.luban.codegen.test.api.order.response.TestOrderPageResponse;
import com.luban.codegen.test.api.order.response.TestOrderResponse;
import com.luban.codegen.test.domain.TestOrder;
import com.luban.codegen.test.domain.command.CreateTestOrderCommand;
import com.luban.codegen.test.domain.command.UpdateTestOrderCommand;
import com.luban.common.base.mapper.DateMapper;
import com.luban.common.base.mapper.GenericEnumMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author hp
 */
@Mapper(
        uses = {
                GenericEnumMapper.class,
                DateMapper.class
        }
)
public interface TestOrderMapper {
    TestOrderMapper INSTANCE = Mappers.getMapper(TestOrderMapper.class);

    TestOrder copy(TestOrder entity);

    CreateTestOrderCommand requestToCreateCommand(TestOrderCreateRequest request);

    TestOrder createCommandToEntity(CreateTestOrderCommand command);

    UpdateTestOrderCommand requestToUpdateCommand(TestOrderUpdateRequest request);

    TestOrderResponse entityToResponse(TestOrder entity);

    default TestOrderResponse entityToCustomResponse(TestOrder entity) {
        final TestOrderResponse response = entityToResponse(entity);
        // customization
        return response;
    }

    TestOrderPageResponse entityToPageResponse(TestOrder entity);

    default TestOrderPageResponse entityToCustomPageResponse(TestOrder entity) {
        final TestOrderPageResponse response = entityToPageResponse(entity);
        // customization
        return response;
    }
}
