package com.foros.client.generator.ant

import com.foros.client.generator.Generator
import com.foros.client.generator.schema.Schema
import com.foros.client.generator.template.model.ModelGenerator

class ModelGeneratorTask extends AbstractGeneratorTask {

    String sourcesDir
    String packageName

    @Override protected Generator createGenerator(Schema schema) {
        return new ModelGenerator(schema, sourcesDir, packageName)
    }
}
