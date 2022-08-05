package com.foros.action.xml;

import com.foros.action.BaseActionSupport;
import com.foros.action.xml.annotation.Model;
import com.foros.framework.ReadOnly;

import java.util.logging.Logger;

import com.opensymphony.xwork2.Action;

public abstract class AbstractXmlAction<T> extends BaseActionSupport {
    protected static final int PAGE_SIZE = 100;

    protected static final int AUTOCOMPLETE_SIZE = 20;

    protected Logger logger;
    private int page = 1;

    /**
     * Model for xml generation
     */
    protected Object model;

    /**
     * @return generated model for response
     * @throws ProcessException if generation fail
     */
    protected abstract T generateModel() throws ProcessException;
    
    public AbstractXmlAction() {
        logger = Logger.getLogger(getClass().getName());
    }
    
    /**
     * Generate xml as string. Log generation and validate required parameters.
     * @return SUCCESS if generation complete and ERROR if not
     */
    @ReadOnly
    public String process() throws ProcessException {
        try {
            model = generateModel();
        } catch (ProcessException e) {
            if (!hideException(e)) {
                throw e;
            }
        }
        return Action.SUCCESS;
    }

    protected boolean hideException(ProcessException e) {
        return false;
    }

    /**
     * @return result model, annotated for XmlResult
     */
    @Model
    public Object getResultModel() {
        return model;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getFirstResult() {
        return (page-1)*PAGE_SIZE;
    }

    /**
     * returns PAGE_SIZE + 1 to avoid using count query. simply test if query
     * return last element to know if next page is needed
     * 
     * @return
     */
    public int getMaxResults() {
        return getFirstResult() + PAGE_SIZE + 1;
    }
}
