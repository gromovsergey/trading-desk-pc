package com.foros.rs.client.service;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.util.Uploader;

public interface UploaderBuilder<E extends EntityBase> {

    Uploader<E> uploader();

}
