package com.foros.session.creative;


import javax.ejb.Local;

@Local
public interface TemporaryCreativePreviewCleaner {
    public void proceed();
}
