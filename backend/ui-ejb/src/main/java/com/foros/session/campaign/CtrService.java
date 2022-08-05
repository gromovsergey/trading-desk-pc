package com.foros.session.campaign;

import java.sql.Timestamp;
import javax.ejb.Local;

@Local
public interface CtrService {

     void resetCtr(Long id, Timestamp version);
}
