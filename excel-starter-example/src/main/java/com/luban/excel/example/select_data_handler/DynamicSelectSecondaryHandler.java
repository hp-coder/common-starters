
package com.luban.excel.example.select_data_handler;

import com.luban.excel.handler.ColumnDynamicSelectDataHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author hp
 */
@Component
public class DynamicSelectSecondaryHandler implements ColumnDynamicSelectDataHandler<String, Map<String, List<String>>> {
    @Override
    public Function<String, Map<String, List<String>>> source() {
        //这里就做数据的查询，最后返回一个Map<String,List<String>即可 key：第一级选择的值，value：第一级选择后对应展示的下拉列表， 对于该方法的入参param没设计好，暂时影响不大
        return param -> new HashMap<String, List<String>>(3) {
            private static final long serialVersionUID = 8084639871057329584L;
            {
                put("级联第一级1", Arrays.asList("级联第一级1-第二级1", "级联第一级1-第二级2", "级联第一级1-第二级3"));
                put("级联第一级2", Arrays.asList("级联第一级2-第二级1", "级联第一级2-第二级2", "级联第一级2-第二级3"));
                put("级联第一级3", Arrays.asList("级联第一级3-第二级1", "级联第一级3-第二级2", "级联第一级3-第二级3"));

            }
        };
    }
}
