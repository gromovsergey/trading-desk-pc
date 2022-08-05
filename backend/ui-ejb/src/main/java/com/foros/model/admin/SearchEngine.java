package com.foros.model.admin;

import com.foros.model.EntityBase;
import com.foros.model.IdNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.PatternConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.ValuesConstraint;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "SEARCHENGINE")

@NamedQueries({@NamedQuery(name = "SearchEngine.list", query = "select se from SearchEngine se order by name, host asc")})
public class SearchEngine extends VersionEntityBase implements IdNameEntity, Serializable {

    @Id
    @SequenceGenerator(name = "SearchEngineGen", sequenceName = "SEARCHENGINE_SEARCH_ENGINE_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SearchEngineGen")
    @Column(name = "SEARCH_ENGINE_ID", nullable = false)
    @IdConstraint
    private Long id;

    @Column(name = "NAME")
    @RequiredConstraint
    @NameConstraint
    @PatternConstraint(regexp = "[\\x20-\\x7E]*", message = "errors.field.invalid")
    private String name;

    @Column(name = "HOST")
    @ByteLengthConstraint(length = 4000)
    @PatternConstraint(regexp = "([\\x20-\\x29\\x2B-\\x2D\\x2F-\\x7E]+(?:[.](?:[\\x20-\\x29\\x2B-\\x2D\\x2F-\\x7E]+|[*]))*)?", message = "errors.field.invalid")
    @RequiredConstraint
    private String host;

    @Column(name = "REGEXP")
    @RequiredConstraint
    @ByteLengthConstraint(length = 4000)
    @PatternConstraint(regexp = "[\\x20-\\x7E]*", message = "errors.field.invalid")
    private String regexp;

    @Column(name = "ENCODING")
    @PatternConstraint(regexp = "[\\x20-\\x7E]*", message = "errors.field.invalid")
    @ByteLengthConstraint(length = 200)
    private String encoding;

    @Column(name = "DECODING_DEPTH")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "99")
    private Long decodingDepth = 1l;

    @Column(name = "POST_ENCODING")
    @PatternConstraint(regexp = "(?:html|js_unicode:.u)*", message = "errors.field.invalid")
    private String postEncoding;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        registerChange("id");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        registerChange("name");
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        registerChange("host");
        this.host = host;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        registerChange("regexp");
        this.regexp = regexp;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        registerChange("encoding");
        this.encoding = encoding;
    }

    public Long getDecodingDepth() {
        return decodingDepth;
    }

    public void setDecodingDepth(Long decodingDepth) {
        registerChange("decodingDepth");
        this.decodingDepth = decodingDepth;
    }

    public String getPostEncoding() {
        return postEncoding;
    }

    public void setPostEncoding(String postEncoding) {
        registerChange("postEncoding");
        this.postEncoding = postEncoding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchEngine that = (SearchEngine) o;

        if (this.getId() != that.getId() && (this.getId() == null || !this.getId().equals(that.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
}
