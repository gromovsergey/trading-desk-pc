package com.foros.session.status;

import javax.ejb.Local;

@Local
public interface Approvable {
    void approve(Long id);

    void decline(Long id, String reason);
}
