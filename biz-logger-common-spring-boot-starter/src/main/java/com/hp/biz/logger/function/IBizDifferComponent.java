package com.hp.biz.logger.function;

import com.hp.biz.logger.model.BizDiffDTO;
import com.hp.biz.logger.model.BizDiffObjectWrapper;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author hp
 */
public interface IBizDifferComponent {

    Collection<BizDiffDTO> diff(@Nullable BizDiffObjectWrapper diffObjectWrapper);
}
