package com.foros.model.report.birt;

import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.model.security.User;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "BIRTREPORTSESSION")
@NamedQueries({
        @NamedQuery(
                name = "BirtReportSession.findBySessionId",
                query = "select crs from BirtReportSession crs where " +
                        " crs.sessionId = :sessionId " +
                        " and crs.state <> com.foros.model.report.birt.BirtReportSessionState.EXPIRED"
        ),
        @NamedQuery(
                name = "BirtReportSession.findStartedByReportIdAndParametersHash",
                query = "select crs from BirtReportSession crs where " +
                        " crs.report.id = :reportId " +
                        " and crs.parametersHash = :parametersHash " +
                        " and crs.state = com.foros.model.report.birt.BirtReportSessionState.STARTED"
        )
})
public class BirtReportSession extends EntityBase implements Identifiable {

    @Id
    @Column(name = "BIRT_REPORT_SESSION_ID", nullable = false)
    @SequenceGenerator(name = "BirtReportSessionGen", sequenceName = "BIRTREPORTSESSION_BIRT_REPORT_SESSION_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BirtReportSessionGen")
    private Long id;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BIRT_REPORT_ID")
    private BirtReport report;

    @OneToOne
    @JoinColumn(name = "BIRT_REPORT_INSTANCE_ID", nullable = true)
    private BirtReportInstance birtReportInstance;

    @Column(name = "PARAMETERS_HASH", nullable = true)
    private String parametersHash;

    @Column(name = "CREATED_TIMESTAMP", insertable = false)
    private Date created;

    @Enumerated
    @Column(name = "STATE")
    private BirtReportSessionState state = BirtReportSessionState.CREATED;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getSessionId() {
        return sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        this.registerChange("sessionId");
    }

    public BirtReport getReport() {
        return report;
    }

    public void setReport(BirtReport report) {
        this.report = report;
        this.registerChange("report");
    }

    public BirtReportInstance getBirtReportInstance() {
        return birtReportInstance;
    }

    public void setBirtReportInstance(BirtReportInstance birtReportInstance) {
        this.birtReportInstance = birtReportInstance;
        this.registerChange("birtReportInstance");
    }

    public String getParametersHash() {
        return parametersHash;
    }

    public void setParametersHash(String parametersHash) {
        this.parametersHash = parametersHash;
        this.registerChange("parametersHash");
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
        this.registerChange("created");
    }

    public BirtReportSessionState getState() {
        return state;
    }

    public void setState(BirtReportSessionState state) {
        this.state = state;
        this.registerChange("state");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BirtReportSession that = (BirtReportSession) o;

        if(id == null || that.getId() == null) return false;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
