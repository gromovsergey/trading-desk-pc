package com.foros.service.mock;

import org.easymock.EasyMock;
import org.springframework.beans.factory.FactoryBean;

public class MockFactory implements FactoryBean {
    private Class clazz;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    @Override
  public Object getObject() throws Exception {
      return EasyMock.createMock(clazz);
  }

    @Override
    public Class getObjectType() {
        return clazz;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
