package com.foros.client.generator.template.model

import com.foros.client.generator.Generator
import com.foros.client.generator.schema.Schema
import com.foros.client.generator.template.java.JavaTemplate

class ModelGenerator implements Generator {

    private Schema schema

    private String packageName
    private String className
    private File sourcesDir

    private JavaTemplate modelTemplate;
    private JavaTemplate errorsTemplate;

    ModelGenerator(Schema schema, String sourcesDir, String packageName) {
        this.sourcesDir = new File(sourcesDir)
        this.schema = schema
        this.packageName = packageName
        this.className = className

        modelTemplate = new JavaTemplate("templates/model/builder.tmpl", packageName)
        errorsTemplate = new JavaTemplate("templates/model/ParseErrorByType.tmpl", packageName)
    }

    void generate() {
        modelTemplate.writeFile(sourcesDir, [type: [name: 'GeneratedModelBuilder'], schema: schema])
        errorsTemplate.writeFile(sourcesDir, [type: [name: 'ParseErrorByType'], schema: schema])
    }
}
