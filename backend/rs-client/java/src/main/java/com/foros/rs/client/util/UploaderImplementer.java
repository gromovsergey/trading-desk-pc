package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;

public interface UploaderImplementer<T extends EntityBase> {

    OperationsResult upload(Operations<T> operations);
}
