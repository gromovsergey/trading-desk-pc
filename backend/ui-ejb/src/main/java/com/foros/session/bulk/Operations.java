package com.foros.session.bulk;

import com.foros.model.EntityBase;
import com.foros.validation.constraint.violation.parsing.ParseErrorsContainer;

import com.foros.validation.constraint.SizeConstraint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Operations<T extends EntityBase> extends ParseErrorsContainer implements Iterable<Operation<T>> {

    @SizeConstraint(max = 1000, message = "errors.operations.count.max")
    private List<Operation<T>> operations = new ArrayList<Operation<T>>();

    @XmlElement(name = "operation")
    public List<Operation<T>> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation<T>> operations) {
        this.operations = operations;
    }

    @Override
    public Iterator<Operation<T>> iterator() {
        return operations.iterator();
    }
}
