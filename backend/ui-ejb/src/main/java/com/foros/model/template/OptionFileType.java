package com.foros.model.template;

import com.foros.annotations.Audit;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.audit.serialize.serializer.entity.OptionFileTypeAuditSerializer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "OPTIONFILETYPE")
@Audit(serializer = OptionFileTypeAuditSerializer.class)
public class OptionFileType implements Serializable {
    @SequenceGenerator(name = "OptionFileTypeGen", sequenceName = "OPTIONFILETYPE_OPTIONFILETYPE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OptionFileTypeGen")
    @Column(name = "OPTIONFILETYPE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @JoinColumn(name = "OPTION_ID", referencedColumnName = "OPTION_ID", updatable = false, nullable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private Option option;

    @Column(name = "FILE_TYPE")
    private String fileType;

    public OptionFileType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OptionFileType)) {
            return false;
        }

        OptionFileType that2 = (OptionFileType) o;

        return ObjectUtils.equals(fileType, that2.fileType);
    }

    @Override
    public int hashCode() {
        return fileType != null ? fileType.hashCode() : 0;
    }
}
