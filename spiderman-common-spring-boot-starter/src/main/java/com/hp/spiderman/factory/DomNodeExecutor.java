package com.hp.spiderman.factory;

import org.jsoup.nodes.Document;

/**
 * @author hp
 */
public interface DomNodeExecutor<DATA> {

    void execute(DATA source, Document document);

    default int runOnLevel(){return 0;}
}
