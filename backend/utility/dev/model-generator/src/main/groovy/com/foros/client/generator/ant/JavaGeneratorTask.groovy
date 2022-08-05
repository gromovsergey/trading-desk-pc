package com.foros.client.generator.ant

import com.foros.client.generator.Generator
import com.foros.client.generator.schema.Schema
import com.foros.client.generator.template.java.JavaGenerator

class JavaGeneratorTask extends AbstractGeneratorTask {

    String sourcesDir
    String resourcesDir
    String packageName

    @Override protected Generator createGenerator(Schema schema) {
        return new JavaGenerator(schema, sourcesDir, resourcesDir, packageName)
    }

}
