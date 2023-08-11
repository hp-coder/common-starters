package com.luban.spiderman.factory;

import org.jsoup.Connection;

import java.util.List;

/**
 * @author hp
 */
public interface SpiderManService {

    <T> List<T> spider(Class<T> clazz, List<Connection> connections);

    <T> List<T> spider(Class<T> clazz);

    <T> void register(Class<T> clazz);

}
