package com.foros.util.comparator;

import com.foros.util.Resolver;

import java.util.Comparator;

/**
 * Author: Boris Vanin
 * Date: 18.12.2008
 * Time: 15:07:52
 * Version: 1.0
 */
public abstract class AbstractResolveComparator<T> implements Comparator<T> {
    protected Resolver resolver;

    public AbstractResolveComparator(Resolver resolver) {
        this.resolver = resolver;
    }
    
}
