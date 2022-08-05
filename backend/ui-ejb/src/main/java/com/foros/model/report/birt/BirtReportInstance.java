package com.foros.model.report.birt;

import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "BIRTREPORTINSTANCE")
@NamedQueries({
        @NamedQuery(
                name = "BirtReportInstance.findByReportId",
                query = "select cri from BirtReportInstance cri where cri.report.id = :reportId"
        )
})
public class BirtReportInstance extends EntityBase implements Identifiable {

    @Id
    @Column(name = "BIRT_REPORT_INSTANCE_ID", nullable = false)
    @SequenceGenerator(name = "BirtReportInstanceGen", sequenceName = "BIRTREPORTINSTANCE_BIRT_REPORT_INSTANCE_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BirtReportInstanceGen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BIRT_REPORT_ID", nullable = false)
    private BirtReport report;

    @Column(name = "parameters_hash", nullable = false)
    private String parametersHash;

    @Column(name = "DOCUMENT_FILE_NAME", nullable = false)
    private String documentFileName;

    @Column(name = "CREATED_TIMESTAMP", insertable = false)
    private Date created;

    @Column(name = "STATE")
    private BirtReportInstanceState state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public BirtReport getReport() {
        return report;
    }

    public void setReport(BirtReport report) {
        this.report = report;
        this.registerChange("report");
    }

    public String getDocumentFileName() {
        return documentFileName;
    }

    public String getParametersHash() {
        return parametersHash;
    }

    public void setParametersHash(String parametersHash) {
        this.parametersHash = parametersHash;
        this.registerChange("parametersHash");
    }

    public void setDocumentFileName(String documentFileName) {
        this.documentFileName = documentFileName;
        this.registerChange("documentFileName");
    }

    public void generateDefaultDocumentFileName() {
        setDocumentFileName("report-document-" + report.getId() + "-" + parametersHash + "-" + System.currentTimeMillis() + ".rptdocument");
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
        this.registerChange("created");
    }

    public BirtReportInstanceState getState() {
        return state;
    }

    public void setState(BirtReportInstanceState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BirtReportInstance instance = (BirtReportInstance) o;

        if(id == null || instance.getId() == null) return false;

        if (id != null ? !id.equals(instance.id) : instance.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
