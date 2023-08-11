package com.luban.spiderman.factory;

import org.jsoup.Connection;

import java.util.List;

/**
 * This factory defines how to create a Jsoup connection.
 *
 * @author hp
 */
public interface ConnectionFactory {

    <TYPE> List<Connection> createFor(Class<TYPE> data);

    <TYPE, SOURCE> List<Connection> createFor(Class<TYPE> data, SOURCE source);

}
