package com.hp.spiderman.factory.support;

import com.hp.spiderman.factory.DomNodeExecutor;
import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This parent class defines the processing logic.
 *
 * @author hp
 */
public abstract class AbstractDomNodeExecutor<SOURCE, DOM_NODE, DOM_DATA, CONVERT_RESULT, RESULT> implements DomNodeExecutor<SOURCE> {

    protected abstract List<DOM_NODE> retrieveNode(Document data);

    protected abstract DOM_DATA retrieveDataByNode(DOM_NODE node);

    protected abstract CONVERT_RESULT convertToResult(DOM_DATA domData);

    protected abstract RESULT beforeOnFound(CONVERT_RESULT convertResult);

    protected abstract void onFound(SOURCE data, List<RESULT> convertResult);

    @Override
    public void execute(SOURCE source, Document document) {
        final List<DOM_DATA> collect = retrieveNode(document)
                .stream()
                .filter(Objects::nonNull)
                .map(this::retrieveDataByNode)
                .collect(Collectors.toList());
        final List<CONVERT_RESULT> convertResults = collect
                .stream()
                .filter(Objects::nonNull)
                .map(this::convertToResult)
                .collect(Collectors.toList());
        final List<RESULT> results = convertResults
                .stream()
                .filter(Objects::nonNull)
                .map(this::beforeOnFound)
                .flatMap(i -> {
                    if (Collection.class.isAssignableFrom(i.getClass())) {
                        return ((Collection<RESULT>) i).stream();
                    } else {
                        return Collections.singletonList(i).stream();
                    }
                })
                .collect(Collectors.toList());
        onFound(source, results);
    }
}
