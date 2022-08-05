package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Operation;
import java.util.List;

public interface UploadStrategy<T extends EntityBase> {

    List<Long> upload(List<Operation<T>> operations, UploaderImplementer<T> uploaderImplementer, UploadOperationEvents<T> events);

}
