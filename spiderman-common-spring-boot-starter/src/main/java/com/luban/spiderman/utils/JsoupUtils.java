package com.luban.spiderman.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import java.util.Optional;

/**
 * This class and its methods are used in SpEL processing
 * so the IDE would give out a no-usages warning.
 * <p>
 *
 * @author hp
 * @see com.luban.spiderman.annotations.SpiderConfig
 * @see com.luban.spiderman.annotations.SpiderNode
 * <p>
 * Just keep the Open Close Principle in mind when updating the class.
 */
@Slf4j
@UtilityClass
public class JsoupUtils {

    public static String url(Element element) {
        final String url = Optional.ofNullable(element).map(Element::baseUri).orElse(null);
        log.debug("url:{}", url);
        return url;
    }

    public static String html(Element element) {
        final String html = element.html();
        log.debug("html:{}", html);
        return html;
    }

    public static String text(Element element) {
        final String text = element.text();
        log.debug("text:{}", text);
        return text;
    }

    public static String attrVal(Element element, String attrName) {
        final String attrVal = element.attr(attrName);
        log.debug("attrName:{}, attrVal:{}", attrName, attrVal);
        return attrVal;
    }
}
