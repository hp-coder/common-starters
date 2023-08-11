package com.luban.spiderman.factory.support;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * It's just a template designed for adaptability.
 * The actual functions are provided by factories.
 * <p>
 * See {@link SpiderNodeBasedDomNodeExecutorFactory}
 *
 * @author hp
 */
@Getter
@Builder
@Slf4j
public class DefaultDomNodeExecutorAdapter<SOURCE, DOM_NODE, DOM_DATA, CONVERT_RESULT, RESULT> extends AbstractDomNodeExecutor<SOURCE, DOM_NODE, DOM_DATA, CONVERT_RESULT, RESULT> {
    private final String name;
    private final int runLevel;

    private final Function<Document, List<DOM_NODE>> retrieveNode;
    private final Function<DOM_NODE, DOM_DATA> retrieveDataByNode;
    private final Function<DOM_DATA, CONVERT_RESULT> convertToResult;
    private final Function<CONVERT_RESULT, RESULT> beforeOnFound;
    private final BiConsumer<SOURCE, List<RESULT>> onFound;

    public DefaultDomNodeExecutorAdapter(
            String name,
            Integer runLevel,
            Function<Document, List<DOM_NODE>> retrieveNode,
            Function<DOM_NODE, DOM_DATA> retrieveDataByNode,
            Function<DOM_DATA, CONVERT_RESULT> convertToResult,
            Function<CONVERT_RESULT, RESULT> beforeOnFound,
            BiConsumer<SOURCE, List<RESULT>> onFound
    ) {
        this.name = name;
        this.runLevel = runLevel == null ? 0 : runLevel;
        this.retrieveNode = retrieveNode;
        this.retrieveDataByNode = retrieveDataByNode;
        this.convertToResult = convertToResult;
        this.beforeOnFound = beforeOnFound;
        this.onFound = onFound;
    }

    @Override
    protected List<DOM_NODE> retrieveNode(Document data) {
        return retrieveNode.apply(data);
    }

    @Override
    protected DOM_DATA retrieveDataByNode(DOM_NODE domNode) {
        return retrieveDataByNode.apply(domNode);
    }

    @Override
    protected CONVERT_RESULT convertToResult(DOM_DATA domData) {
        return convertToResult.apply(domData);
    }

    @Override
    protected RESULT beforeOnFound(CONVERT_RESULT convertResult) {
        return beforeOnFound.apply(convertResult);
    }

    @Override
    protected void onFound(SOURCE data, List<RESULT> convertResult) {
        onFound.accept(data, convertResult);
    }

    @Override
    public int runOnLevel() {
        return this.runLevel;
    }

    @Override
    public String toString() {
        return String.format("DomNodeExecutorAdapter-for-%s", name);
    }
}
