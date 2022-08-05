package com.foros.framework;

import com.foros.security.AccountRole;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.util.StringUtil;
import com.foros.util.UIConstants;
import com.foros.util.context.RequestContexts;

import javax.servlet.http.HttpServletRequest;
import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;

public class CustomViewPreparer implements ViewPreparer {
    @Override
    public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        prepareModuleName(tilesContext, attributeContext);
        prepareContext(tilesContext, attributeContext);
        prepareUserMenu(attributeContext);
    }

    private void prepareModuleName(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        Attribute moduleNameAttr = attributeContext.getAttribute("moduleName");
        if (moduleNameAttr == null) {
            return;
        }

        String moduleName = (String) moduleNameAttr.getValue();
        String context = (String) tilesContext.getRequestScope().get(UIConstants.CONTEXT_REQUEST_PARAMETER);

        if (StringUtil.isPropertyEmpty(context)) {
            return;
        }

        context = context.substring(1);

        if (!moduleName.startsWith(context)) {
            attributeContext.putAttribute("moduleName", new Attribute(context + moduleName));
        }
    }

    private void prepareContext(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        if (attributeContext.getAttribute("contextName") != null) {
            return;
        }

        RequestContexts contexts = RequestContexts.getRequestContexts((HttpServletRequest) tilesContext.getRequest());

        boolean contextMenu = true;
        String contextName = null;

        if (!contexts.isStandalone()) {
            if (contexts.getAdvertiserContext().isSet()) {
                contextName = "global.menu.advertisers";
            } else if (contexts.getPublisherContext().isSet()) {
                contextName = "global.menu.publishers";
            } else if (contexts.getCmpContext().isSet()) {
                contextName = "global.menu.cmps";
            } else if (contexts.getIspContext().isSet()) {
                contextName = "global.menu.isps";
            }
        }

        if (contextName == null) {
            contextMenu = false;
            contextName = "global.menu.admin"; // compatibility
        }

        attributeContext.putAttribute("contextName", new Attribute(contextName));

        if (contextMenu) {
            attributeContext.putAttribute("accountContextMenu", new Attribute("/menu/accountContextMenu.jsp"));
        }
    }

    private void prepareUserMenu(AttributeContext attributeContext) {
        if (!SecurityContext.isAuthenticatedAndNotAnonymous()) {
            return;
        }

        AccountRole role = SecurityContext.getAccountRole();

        String userMenu;
        switch (role) {
            case INTERNAL:
                userMenu = "/menu/internalMenu.jsp";
                break;
            case AGENCY:
            case ADVERTISER:
                userMenu = "/menu/advertiserMenu.jsp";
                break;
            case PUBLISHER:
                userMenu = "/menu/publisherMenu.jsp";
                break;
            case ISP:
                userMenu = "/menu/ispMenu.jsp";
                break;
            case CMP:
                userMenu = "/menu/cmpMenu.jsp";
                break;
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }

        attributeContext.putAttribute("menu", new Attribute(userMenu));
    }
}
