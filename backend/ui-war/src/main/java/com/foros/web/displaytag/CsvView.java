package com.foros.web.displaytag;

import org.displaytag.model.TableModel;

/**
 *
 * @author Andrey Chernyshov
 */
public class CsvView extends org.displaytag.export.CsvView {
    /**
     * TableModel to render.
     */
    private TableModel model;

    public void setParameters(TableModel tableModel, boolean exportFullList, boolean includeHeader, boolean decorateValues) {
        this.model = tableModel;
        super.setParameters(tableModel, exportFullList, includeHeader, decorateValues);
    }

    protected String getDocumentStart() {
        return model.getCaption();
    }

    protected String getRowEnd() {
        return "\r\n";
    }
}
