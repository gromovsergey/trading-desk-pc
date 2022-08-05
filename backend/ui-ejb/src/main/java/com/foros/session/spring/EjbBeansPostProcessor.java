package com.foros.session.spring;

import java.util.ArrayList;
import java.util.Collection;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

public class EjbBeansPostProcessor implements BeanFactoryPostProcessor {

    private static final String EJB_APP_LOCATION = "java:global/foros-ui/foros-ui-ejb";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            Collection<String> names = getEjbJndiNames();
            for (String jndiName : names) {
                BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(JndiObjectFactoryBean.class);
                bdb.addPropertyValue("jndiName", jndiName);
                bdb.addPropertyValue("lookupOnStartup", true);
                bdb.setScope(BeanDefinition.SCOPE_SINGLETON);
                ((BeanDefinitionRegistry)beanFactory).registerBeanDefinition(jndiName, bdb.getBeanDefinition());
            }
        } catch (NamingException e) {
            throw new BeanInitializationException("Can't initialize ejb", e);
        }
    }

    private Collection<String> getEjbJndiNames() throws NamingException {
        JndiTemplate jndiTemplate = new JndiTemplate();
        NamingEnumeration<NameClassPair> enumeration = jndiTemplate.getContext().list(EJB_APP_LOCATION);

        ArrayList<String> result = new ArrayList<String>();
        while (enumeration.hasMore()) {
            NameClassPair pair = enumeration.next();

            if (isRealEjb(pair.getName())) {
                result.add(EJB_APP_LOCATION + "/" + pair.getName());
            }
        }

        return result;
    }

    private boolean isRealEjb(String jndiName) {
        String[] parts = jndiName.split("!");

        if (parts.length == 2) {
            String beanName = parts[0];
            String interfaceName = parts[1];

            if (interfaceName.endsWith(beanName)) {
                return true;
            }
        }

        return false;
    }

}
