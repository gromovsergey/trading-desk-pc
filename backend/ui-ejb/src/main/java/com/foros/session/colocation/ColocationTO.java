package com.foros.session.colocation;

import com.foros.model.Status;
import com.foros.model.isp.Colocation;
import com.foros.session.DisplayStatusEntityTO;
import com.foros.util.DSTimeInterval;

/**
 * @author dmitry_antonov
 * @since 07.02.2008
 */
public class ColocationTO extends DisplayStatusEntityTO {

    private String softwareVersion;
    private DSTimeInterval lastUpdate;
    private DSTimeInterval lastStats;

    public ColocationTO(Long id, String name, char status, String softwareVersion, DSTimeInterval lastUpdate, DSTimeInterval lastStats) {
        super(id, name, status, Colocation.getDisplayStatus(Status.valueOf(status)));
        this.lastUpdate = lastUpdate;
        this.lastStats = lastStats;
        this.softwareVersion = softwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public DSTimeInterval getLastUpdate() {
        return lastUpdate;
    }

    public DSTimeInterval getLastStats() {
        return lastStats;
    }
}
