package com.foros.framework.tiles;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.compat.definition.digester.CompatibilityDigesterDefinitionsReader;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PrefixedPatternDefinitionResolver;
import org.apache.tiles.definition.pattern.wildcard.WildcardDefinitionPatternMatcherFactory;
import org.apache.tiles.el.ELAttributeEvaluator;
import org.apache.tiles.el.JspExpressionFactoryFactory;
import org.apache.tiles.el.TilesContextBeanELResolver;
import org.apache.tiles.el.TilesContextELResolver;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.factory.TilesContainerFactoryException;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.ognl.ApplicationScopeNestedObjectExtractor;
import org.apache.tiles.ognl.DelegatePropertyAccessor;
import org.apache.tiles.ognl.NestedObjectDelegatePropertyAccessor;
import org.apache.tiles.ognl.OGNLAttributeEvaluator;
import org.apache.tiles.ognl.PropertyAccessorDelegateFactory;
import org.apache.tiles.ognl.RequestScopeNestedObjectExtractor;
import org.apache.tiles.ognl.SessionScopeNestedObjectExtractor;
import org.apache.tiles.ognl.TilesApplicationContextNestedObjectExtractor;
import org.apache.tiles.ognl.TilesContextPropertyAccessorDelegateFactory;
import org.apache.tiles.renderer.AttributeRenderer;
import org.apache.tiles.renderer.TypeDetectingAttributeRenderer;
import org.apache.tiles.renderer.impl.BasicRendererFactory;
import org.apache.tiles.renderer.impl.ChainedDelegateAttributeRenderer;
import org.apache.tiles.util.URLUtil;

public class ForosTilesContainerFactory extends BasicTilesContainerFactory {

    /** {@inheritDoc} */
    @Override
    protected BasicTilesContainer instantiateContainer(TilesApplicationContext applicationContext) {
        return new CachingTilesContainer();
    }


    /** {@inheritDoc} */
    @Override
    protected AttributeRenderer createDefaultAttributeRenderer(
            BasicRendererFactory rendererFactory,
            TilesApplicationContext applicationContext,
            TilesRequestContextFactory contextFactory,
            TilesContainer container,
            AttributeEvaluatorFactory attributeEvaluatorFactory) {

        ChainedDelegateAttributeRenderer retValue = new ChainedDelegateAttributeRenderer();
        retValue.addAttributeRenderer((TypeDetectingAttributeRenderer) rendererFactory.getRenderer(DEFINITION_RENDERER_NAME));
        retValue.addAttributeRenderer((TypeDetectingAttributeRenderer) rendererFactory.getRenderer(TEMPLATE_RENDERER_NAME));
        retValue.addAttributeRenderer((TypeDetectingAttributeRenderer) rendererFactory.getRenderer(STRING_RENDERER_NAME));
        retValue.setApplicationContext(applicationContext);
        retValue.setRequestContextFactory(contextFactory);
        retValue.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        return retValue;
    }

    /** {@inheritDoc} */
    @Override
    protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(
            TilesApplicationContext applicationContext,
            TilesRequestContextFactory contextFactory,
            LocaleResolver resolver) {

        BasicAttributeEvaluatorFactory attributeEvaluatorFactory = new BasicAttributeEvaluatorFactory(createELEvaluator(applicationContext));
        attributeEvaluatorFactory.registerAttributeEvaluator("OGNL", createOGNLEvaluator());

        return attributeEvaluatorFactory;
    }

    /** {@inheritDoc} */
    @Override
    protected <T> PatternDefinitionResolver<T> createPatternDefinitionResolver(
            Class<T> customizationKeyClass) {

        PrefixedPatternDefinitionResolver<T> resolver = new PrefixedPatternDefinitionResolver<T>();
        resolver.registerDefinitionPatternMatcherFactory("WILDCARD", new WildcardDefinitionPatternMatcherFactory());
        return resolver;
    }

    /** {@inheritDoc} */
    @Override
    protected List<URL> getSourceURLs(TilesApplicationContext applicationContext,
            TilesRequestContextFactory contextFactory) {
        try {
            Set<URL> finalSet = new HashSet<URL>();
            Set<URL> webINFSet = applicationContext.getResources("/WEB-INF/tiles2/tiles*.xml");

            if (webINFSet != null) {
                finalSet.addAll(webINFSet);
            }

            return URLUtil.getBaseTilesDefinitionURLs(finalSet);
        } catch (IOException e) {
            throw new DefinitionsFactoryException("Cannot load definition URLs", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected DefinitionsReader createDefinitionsReader(TilesApplicationContext applicationContext, TilesRequestContextFactory contextFactory) {
        return new CompatibilityDigesterDefinitionsReader();
    }

    /**
     * Creates the EL evaluator.
     *
     * @param applicationContext The Tiles application context.
     * @return The EL evaluator.
     */
    private ELAttributeEvaluator createELEvaluator(
            TilesApplicationContext applicationContext) {

        ELAttributeEvaluator evaluator = new ELAttributeEvaluator();
        evaluator.setApplicationContext(applicationContext);
        JspExpressionFactoryFactory efFactory = new JspExpressionFactoryFactory();
        efFactory.setApplicationContext(applicationContext);
        evaluator.setExpressionFactory(efFactory.getExpressionFactory());
        ELResolver elResolver = new CompositeELResolver() {
            {
                add(new TilesContextELResolver());
                add(new TilesContextBeanELResolver());
                add(new ArrayELResolver(false));
                add(new ListELResolver(false));
                add(new MapELResolver(false));
                add(new ResourceBundleELResolver());
                add(new BeanELResolver(false));
            }
        };
        evaluator.setResolver(elResolver);
        return evaluator;
    }

    /**
     * Creates the OGNL evaluator.
     * TODO: Check how it works with struts context
     * @return The OGNL evaluator.
     */
    private OGNLAttributeEvaluator createOGNLEvaluator() {
        try {
            PropertyAccessor objectPropertyAccessor = OgnlRuntime.getPropertyAccessor(Object.class);
            PropertyAccessor mapPropertyAccessor = OgnlRuntime.getPropertyAccessor(Map.class);
            PropertyAccessor applicationContextPropertyAccessor =
                new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                    new TilesApplicationContextNestedObjectExtractor(),
                    objectPropertyAccessor);
            PropertyAccessor requestScopePropertyAccessor =
                new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                    new RequestScopeNestedObjectExtractor(), mapPropertyAccessor);
            PropertyAccessor sessionScopePropertyAccessor =
                new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                    new SessionScopeNestedObjectExtractor(), mapPropertyAccessor);
            PropertyAccessor applicationScopePropertyAccessor =
                new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                    new ApplicationScopeNestedObjectExtractor(), mapPropertyAccessor);
            PropertyAccessorDelegateFactory<TilesRequestContext> factory =
                new TilesContextPropertyAccessorDelegateFactory(
                    objectPropertyAccessor, applicationContextPropertyAccessor,
                    requestScopePropertyAccessor, sessionScopePropertyAccessor,
                    applicationScopePropertyAccessor);
            PropertyAccessor tilesRequestAccessor = new DelegatePropertyAccessor<TilesRequestContext>(factory);
            OgnlRuntime.setPropertyAccessor(TilesRequestContext.class, tilesRequestAccessor);
            return new OGNLAttributeEvaluator();
        } catch (OgnlException e) {
            throw new TilesContainerFactoryException("Cannot initialize OGNL evaluator", e);
        }
    }

}
