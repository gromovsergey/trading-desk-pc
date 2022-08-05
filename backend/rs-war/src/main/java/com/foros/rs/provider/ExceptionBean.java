package com.foros.rs.provider;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.session.ServiceLocator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Exception", propOrder = {"name", "message"})
@XmlRootElement(name = "exception")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ExceptionBean {
    private static final Logger logger = Logger.getLogger(ExceptionBean.class.getName());
    private static final String DEFAULT_PACKAGE = "com.foros";

    private String name;
    private String message;

    public ExceptionBean() {
    }

    public ExceptionBean(Throwable e) {
        Config config = ServiceLocator.getInstance().lookup(ConfigService.class).detach();
        String apiPackage = config.get(ConfigParameters.API_PACKAGE);
        if (!apiPackage.equals(DEFAULT_PACKAGE)) {
            this.name = e.getClass().getName().replaceAll(DEFAULT_PACKAGE, apiPackage);
            this.message = e.getMessage().replaceAll(DEFAULT_PACKAGE, apiPackage);
        } else {
            this.name = e.getClass().getName();
            this.message = e.getMessage();
        }
        logger.log(Level.SEVERE, this.message, e);
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }
}
