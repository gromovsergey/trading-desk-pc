package com.foros.session.bulk;

public interface BulkOperation<T> {
    void perform(T existing, T toUpdate);
}
