package com.hp.dingtalk.service.oa;

import com.aliyun.dingtalkworkflow_1_0.models.QuerySchemaByProcessCodeResponseBody;

/**
 * @author hp
 */
public interface IDingNewOAHandler {
    QuerySchemaByProcessCodeResponseBody.QuerySchemaByProcessCodeResponseBodyResult getProcessTemplateSchema(String processCode);
}
