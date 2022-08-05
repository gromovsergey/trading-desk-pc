package com.foros.client.generator.template

import com.foros.client.generator.utils.ResourceUtils
import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.apache.commons.io.IOUtils

class SimpleTemplate {
    private Template template

    public SimpleTemplate(String templateName) {
        def text = ResourceUtils.getResource(templateName).text
        template = new SimpleTemplateEngine().createTemplate(text)
    }

    void writeFile(File outputPath, Map bindings = [:]) {
        System.out.println("Generating: " + outputPath);
        FileWriter writer
        try {
            outputPath.getParentFile().mkdirs()
            writer = new FileWriter(outputPath)
            template.make(bindings).writeTo(writer)
        } finally {
            IOUtils.closeQuietly(writer)
        }
    }
}
