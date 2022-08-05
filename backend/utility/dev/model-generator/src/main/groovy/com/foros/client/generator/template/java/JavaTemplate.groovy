package com.foros.client.generator.template.java

import com.foros.client.generator.template.SimpleTemplate
import com.foros.client.generator.utils.ResourceUtils
import org.apache.commons.io.FilenameUtils

class JavaTemplate {
    private static final String ext = ".java"
    private static final LinkedHashMap<String, JavaTemplateUtil> defaultBindings = [util: new JavaTemplateUtil()]

    private SimpleTemplate template;
    private String defaultFileName;
    private String rootPackage;

    JavaTemplate(String templateName, String rootPackage) {
        this.template = new SimpleTemplate(templateName)
        this.defaultFileName = FilenameUtils.getBaseName(templateName) + ext;
        this.rootPackage = rootPackage;
    }

    void writeFile(File outputPath, Map bindings = [:]) {
        def type = bindings.type

        if (type == null) {
            type = [name: defaultFileName]
        }

        def fullPackage = rootPackage + (type.packageName != null ? "." + type.packageName : "")
        def fileName = type.name == null ? defaultFileName : type.name + ext;

        File filePath = new File(outputPath, ResourceUtils.packageToDir(fullPackage) + File.separator + fileName)

        template.writeFile(filePath, bindings + defaultBindings + [packageName: fullPackage, rootPackage: rootPackage])
    }
}
