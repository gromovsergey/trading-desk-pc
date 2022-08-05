package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.result.RsConstraintViolationException;

import java.util.List;

public interface UploadOperationEvents<E extends EntityBase> {

    void onBefore(String name, int count);

    void onBatch(int from, int to);

    void onConstraintViolation(RsConstraintViolationException e);

    void onProcessedOperations(List<Operation<E>> operation);

    void onAfter(String name, int count);

}
