package com.foros.util.xml;

import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * 
 * @author oleg_roshka
 */
public class ClassPathURIResolver implements URIResolver {
    private final String defaultBase;

    public ClassPathURIResolver(String base) {
        this.defaultBase = base;
    }

    public Source resolve(String href, String base) throws TransformerException {
        if (href.equals("")) {
            return null;
        }

        String path = buildPath(href, base);
        return load(path);
    }

    private String buildPath(String href, String base) {
        String path;

        if (href.charAt(0) == '/') {
            path = href;
        } else {
            path = (base == null || base.equals("") ? defaultBase : base) + href;
        }
        
        return path;
    }

    private Source load(String path) throws TransformerException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStreamFromClassPath = classLoader.getResourceAsStream(path);
            if (inputStreamFromClassPath == null) {
                return null;
            }

            return new StreamSource(inputStreamFromClassPath);
        } catch (Exception e) {
            throw new TransformerException(e);
        }
    }
}
