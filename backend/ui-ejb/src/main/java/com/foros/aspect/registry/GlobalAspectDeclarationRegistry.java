package com.foros.aspect.registry;

import com.foros.aspect.AspectException;
import com.foros.aspect.registry.find.AspectDeclarationFinder;
import com.foros.aspect.util.AspectUtil;
import com.foros.util.StringUtil;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Startup
@Singleton(name = "AspectDeclarationRegistry")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GlobalAspectDeclarationRegistry implements AspectDeclarationRegistry {

    private static final String CLASS_SEARCH_PATTERN = "classpath*:com/foros/**/*.class";

    private final Logger logger = Logger.getLogger(GlobalAspectDeclarationRegistry.class.getName());

    private Map<Class<? extends Annotation>, Map<String, AspectDeclarationDescriptor>> declarations;

    @PostConstruct
    public void construct() {
        declarations = initialize();
    }

    protected Map<Class<? extends Annotation>, Map<String, AspectDeclarationDescriptor>> initialize() {
        Map<Class<? extends Annotation>, Map<String, AspectDeclarationDescriptor>> result =
                new HashMap<Class<? extends Annotation>, Map<String, AspectDeclarationDescriptor>>();

        Map<Class, AspectDeclarationInfo> infos = AspectDeclarationFinder.findAspectDeclarations(CLASS_SEARCH_PATTERN);

        for (Map.Entry<Class, AspectDeclarationInfo> entry : infos.entrySet()) {

            AspectDeclarationInfo aspectDeclarationInfo = entry.getValue();

            String objectType = createObjectType(aspectDeclarationInfo, entry.getKey());

            Map<String, AspectDeclarationDescriptor> aspects = result.get(aspectDeclarationInfo.getIndex());

            if (aspects == null) {
                aspects = new HashMap<String, AspectDeclarationDescriptor>();
                result.put(aspectDeclarationInfo.getIndex(), aspects);
            }

            if(aspects.containsKey(objectType)) {
                throw new IllegalStateException("Duplicate of aspect('" + entry.getKey() + "')!");
            }

            // todo: separate different annotations
            Map<String, AspectDeclarationDescriptor> descriptors = createDescriptors(objectType, entry.getKey());
            aspects.putAll(descriptors);
        }

        return result;
    }

    private Map<String, AspectDeclarationDescriptor> createDescriptors(String objectType, Class service) {
        Map<String, Collection<Method>> info = new HashMap<String, Collection<Method>>();

        for (Method method : service.getMethods()) {
            AspectDeclarationInfo aspectDeclaration = AspectUtil.findAspectDeclaration(method);

            if (aspectDeclaration != null) {
                String actionName = createActionName(aspectDeclaration, method);

                String aspectName = createAspectName(objectType, actionName);

                Collection<Method> methods = info.get(aspectName);
                if (methods == null) {
                    methods = new ArrayList<Method>();
                    info.put(aspectName, methods);
                }

                methods.add(method);
            }
        }

        Map<String, AspectDeclarationDescriptor> result = new HashMap<String, AspectDeclarationDescriptor>();
        for (Map.Entry<String, Collection<Method>> entry : info.entrySet()) {
            AspectDeclarationDescriptor descriptor = new AspectDeclarationDescriptor(service, entry.getKey(), entry.getValue());

            logger.fine("Aspect " + descriptor.getName() + " registered.");

            result.put(descriptor.getName(), descriptor);
        }

        return result;
    }

    private String createAspectName(String objectType, String actionName) {
        // todo: check name
        return objectType + "." + actionName;
    }

    private String createActionName(AspectDeclarationInfo declarationInfo, Method method) {
        if (StringUtil.isPropertyNotEmpty(declarationInfo.getName())) {
            return declarationInfo.getName();
        } else {
            return fetchActionNameByConvention(declarationInfo.getConvention(), method.getName());
        }
    }

    private String createObjectType(AspectDeclarationInfo info, Class type) {
        if (StringUtil.isPropertyNotEmpty(info.getName())) {
            return info.getName();
        } else {
            return fetchObjectTypeByConvention(info.getConvention(), type.getSimpleName());
        }
    }

    private String fetchObjectTypeByConvention(Pattern convention, String className) {
        Matcher matcher = convention.matcher(className);

        if (matcher.matches() && matcher.groupCount() > 0) {
            return matcher.group(1);
        }

        throw new RuntimeException("Class without custom aspect name not applied convention! Convention: " + convention);
    }

    private String fetchActionNameByConvention(Pattern convention, String name) {
        Matcher matcher = convention.matcher(name);
        if (matcher.matches() && matcher.groupCount() > 0) {
            String actionName = matcher.group(1);
            return Character.toLowerCase(actionName.charAt(0)) + actionName.substring(1);
        }

        throw new RuntimeException("Method without custom name not applied convention! Convention: " + convention);
    }

    @Override
    public AspectDeclarationDescriptor getDescriptor(Class<? extends Annotation> type, String name) {
        Map<String, AspectDeclarationDescriptor> descriptors = declarations.get(type);
        if (descriptors == null) {
            throw new AspectException("Unknown aspect type " + type.getSimpleName());
        }

        AspectDeclarationDescriptor aspectDeclarationDescriptor = descriptors.get(name);

        if (aspectDeclarationDescriptor == null) {
            throw new AspectException("Unknown " + type.getSimpleName() + " aspect with name " + name);
        }

        return aspectDeclarationDescriptor;
    }
}
