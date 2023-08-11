package com.luban.spiderman.factory.support;

import com.luban.spiderman.factory.DomNodeExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
public class SingleWorldDomNodesExecutor<DATA> extends AbstractDomNodesExecutor<DATA> {

    public SingleWorldDomNodesExecutor(Class<DATA> clazz, List<DomNodeExecutor<DATA>> spiderNodeExecutors) {
        super(clazz, spiderNodeExecutors);
    }

    @Override
    public List<DATA> execute(List<Connection> connections) {
        return connections.stream()
                .map(connection -> {
                    try {
                        final Document doc = connection.get();
                        final DATA source = ConstructorUtils.invokeConstructor(getClazz());
                        getSpiderNodeExecutors().forEach(execute -> execute.execute(source, doc));
                        return source;
                    } catch (Exception e) {
                        log.error("Possible reason: Can't initialize the instance of {} due to lack of no-arg constructor presents", getClazz(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
