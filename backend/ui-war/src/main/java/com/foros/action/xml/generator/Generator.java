package com.foros.action.xml.generator;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 18:30:26
 * Version: 1.0
 */
public interface Generator<T> {

    String generate(T model);

}
