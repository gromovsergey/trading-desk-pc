package com.foros.framework;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.tiles.TilesResult;
import org.apache.tiles.definition.NoSuchDefinitionException;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * This result catches exceptions when non existing tiles template was requiested and returns
 * 404 Error insteadof 500.
 *
 * @author alexey_chernenko
 */
public class CustomTilesResult extends TilesResult {
    @Override
    public void doExecute(String s, ActionInvocation actionInvocation) throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        try {
            super.doExecute(s, actionInvocation);
        } catch (NoSuchDefinitionException ex) {
            Logger.getLogger(CustomTilesResult.class.getName()).log(Level.SEVERE, "Tiles definition for {0} was not found", s);
            response.sendError(404);
        }
    }
}
