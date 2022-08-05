package com.foros.util.context;

import com.foros.security.AccountRole;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.SerializationException;

public abstract class Contexts implements Serializable {

    private AdvertiserContext advertiserContext;
    private PublisherContext publisherContext;
    private IspContext ispContext;
    private CmpContext cmpContext;

    protected Contexts() {
        setAdvertiserContext(new AdvertiserContext());
        setIspContext(new IspContext());
        setPublisherContext(new PublisherContext());
        setCmpContext(new CmpContext());
    }

    public PublisherContext getPublisherContext() {
        return publisherContext;
    }

    protected void setPublisherContext(PublisherContext publisherContext) {
        this.publisherContext = publisherContext;
    }

    public IspContext getIspContext() {
        return ispContext;
    }

    protected void setIspContext(IspContext ispContext) {
        this.ispContext = ispContext;
    }

    public CmpContext getCmpContext() {
        return cmpContext;
    }

    protected void setCmpContext(CmpContext cmpContext) {
        this.cmpContext = cmpContext;
    }

    public AdvertiserContext getAdvertiserContext() {
        return advertiserContext;
    }

    protected void setAdvertiserContext(AdvertiserContext advertiserContext) {
        this.advertiserContext = advertiserContext;
    }

    public static RequestContexts getContexts(HttpServletRequest request) {
        return RequestContexts.getRequestContexts(request);
    }

    @SuppressWarnings({"unchecked"})
    protected static <T extends Serializable> T clone(T sessionContext) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        ObjectOutputStream out = null;
        try {
            // stream closed in the finally
            out = new ObjectOutputStream(baos);
            out.writeObject(sessionContext);

        } catch (IOException ex) {
            throw new SerializationException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        
        byte[] objectData = baos.toByteArray();
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        ObjectInputStream in = null;
        try {
            // stream closed in the finally
            in = new ObjectInputStream(bais);
            return (T)in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new SerializationException(ex);
        } catch (IOException ex) {
            throw new SerializationException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public ContextBase getContext(AccountRole role) {
        ContextBase context;
        switch (role) {
            case AGENCY:
            case ADVERTISER:
                context = getAdvertiserContext();
                break;
            case ISP:
                context = getIspContext();
                break;
            case PUBLISHER:
                context = getPublisherContext();
                break;
            case CMP:
                context = getCmpContext();
                break;
            case INTERNAL:
                // no context
                context = null;
                break;
            default:
                throw new IllegalArgumentException(role.getName());
        }
        return context;
    }


    protected void switchToDefaults() {
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        if (principal != null && !principal.isAnonymous()) {

            ContextBase context = getContext(SecurityContext.getAccountRole());

            if (context != null) {
                context.switchTo(principal.getAccountId());
            }
        }
    }
}
