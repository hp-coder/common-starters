package com.hp.biz.logger.service;

import com.hp.biz.logger.model.BizLogDTO;

import java.util.Collection;

/**
 * @author hp
 */
public interface IBizLogSyncService {

    void sync(Collection<BizLogDTO> bizLogDTOs);
}
