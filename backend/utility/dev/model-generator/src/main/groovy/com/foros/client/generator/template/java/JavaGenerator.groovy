package com.foros.client.generator.template.java

import com.foros.client.generator.Generator
import com.foros.client.generator.schema.Schema

class JavaGenerator implements Generator {

    private Schema schema

    private File sourcesDir
    private File resourcesDir

    // templates
    private final JavaTemplate packageInfoTemplate
    private final JavaTemplate classTemplate
    private final JavaTemplate queryTemplate
    private final JavaTemplate enumTemplate
    private final JavaTemplate jaxbUtilsTemplate

    JavaGenerator(Schema schema, String sourcesDir, String resourcesDir, String packageName) {
        this.schema = schema

        this.sourcesDir = new File(sourcesDir)
        this.resourcesDir = new File(resourcesDir)

        packageInfoTemplate = new JavaTemplate("templates/java/package-info.tmpl", packageName)

        classTemplate = new JavaTemplate("templates/java/class.tmpl", packageName)
        queryTemplate = new JavaTemplate("templates/java/queryClass.tmpl", packageName)
        enumTemplate = new JavaTemplate("templates/java/enum.tmpl", packageName)
        jaxbUtilsTemplate = new JavaTemplate("templates/java/JAXBUtils.tmpl", packageName)
    }

    void generate() {
        // Enums
        schema.simpleTypes.values().each {
            enumTemplate.writeFile(sourcesDir, [type: it])
        }

        // Other java classes
        schema.complexTypes.values().each {
            classTemplate.writeFile(sourcesDir, [type: it, schema: schema])
        }

        // package-info
        def allJaxbTypes = schema.complexTypes.values() + schema.simpleTypes.values()
        allJaxbTypes.collect{ it.packageName }.unique().each {
            packageInfoTemplate.writeFile(sourcesDir, [type: [packageName: it]])
        }

        jaxbUtilsTemplate.writeFile(sourcesDir, [type: [packageName: "rsclient.data"], types: allJaxbTypes])
    }

}
