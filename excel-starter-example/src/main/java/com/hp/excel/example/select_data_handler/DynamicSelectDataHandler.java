package com.hp.excel.example.select_data_handler;

import com.hp.excel.handler.ColumnDynamicSelectDataHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hp
 */
@Component
public class DynamicSelectDataHandler implements ColumnDynamicSelectDataHandler<String, List<String>> {
    @Override
    public Function<String, List<String>> source() {
        //这里就做数据的查询，最后返回一个List<String>即可， 对于该方法的入参param没设计好，暂时影响不大
        return param -> Stream.iterate(0,i-> i+1)
                .limit(200)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }
}
