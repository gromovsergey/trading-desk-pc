package com.foros.session.query;

import com.foros.session.bulk.Paging;

import java.util.List;

public interface QueryExecutor {

    <T> List<T> list();

    <T> PartialList<T> partialList(int from, int count);

    <T> PartialList<T> partialList(Paging paging);

    int count();

    QueryExecutor scrollable();

    QueryExecutor noCount();
}
