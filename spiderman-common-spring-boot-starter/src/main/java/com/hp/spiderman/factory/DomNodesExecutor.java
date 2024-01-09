package com.hp.spiderman.factory;

import org.jsoup.Connection;

import java.util.List;

/**
 * @author hp
 */
public interface DomNodesExecutor<DATA> {

    List<DATA> execute(List<Connection> connections);
}
