package com.foros.util.xslt;

import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.PathProvider;
import com.foros.util.StringUtil;
import org.apache.commons.io.FilenameUtils;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class FileSystemResolver implements URIResolver {
    private FileSystem fileSystem;

    public FileSystemResolver(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public FileSystemResolver(PathProvider pathProvider, String templatePath) {
        this(relativeFs(pathProvider, templatePath));
    }

    public Source resolve(String href, String base) throws TransformerException {
        try {
            return new StreamSource(fileSystem.readFile(href));
        } catch (IOException e) {
            throw new TransformerException("Can't resolve :" + href, e);
        }
    }

    private static FileSystem relativeFs(PathProvider pathProvider, String templatePath) {
        PathProvider resolverPP;
        String parentPath = FilenameUtils.getFullPath(templatePath);
        if (StringUtil.isPropertyEmpty(parentPath)) {
            resolverPP = pathProvider;
        } else {
            resolverPP = pathProvider.getNested(parentPath);
        }
        return resolverPP.createFileSystem();
    }
}
