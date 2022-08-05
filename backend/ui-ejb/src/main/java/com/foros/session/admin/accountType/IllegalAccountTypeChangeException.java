package com.foros.session.admin.accountType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.ApplicationException;

@ApplicationException
public class IllegalAccountTypeChangeException extends RuntimeException {
    
    private List<String> fields = new LinkedList<String>(); 
    
    public IllegalAccountTypeChangeException() {
    }

    public IllegalAccountTypeChangeException(Throwable cause) {
        super(cause);
    }

    public IllegalAccountTypeChangeException(String message) {
        super(message);
    }

    public IllegalAccountTypeChangeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IllegalAccountTypeChangeException(String message, Collection<String> fields) {
        super(message);
        this.fields.addAll(fields);
    }
    
    public IllegalAccountTypeChangeException(String message, String field) {
        super(message);
        this.fields.add(field);
    }

    public List<String> getFields() {
        return fields;
    }
    
    @Override
    public String getMessage(){
        String msg = super.getMessage();
        StringBuilder fields = new StringBuilder(" List of fields [");
        for (String field : getFields()) {
            fields.append(field).append(" ,");
        }
        fields.append("]");
        return msg + fields.toString();
    }
}
