package com.foros.client.generator.schema

import com.foros.client.generator.schema.SchemaAggregator
import com.foros.client.generator.template.java.JavaGenerator
import com.foros.client.generator.template.model.ModelGenerator
import junit.framework.TestCase

class GeneratorTest extends TestCase {

    private Schema schema
    private String generateDir

    @Override
    protected void setUp() {
        super.setUp()
        schema = new SchemaAggregator().readSchema()
        generateDir = new File(getClass().classLoader.getResource("").toURI()).parentFile.absolutePath + "/generated-model";
    }

    def void testJava() {
        new JavaGenerator(schema, generateDir + "/java", generateDir + "/res", "zzz.yyy").generate()
    }

    def void testModel() {
        new ModelGenerator(schema, generateDir + "/model", "zzz.yyy").generate()
    }
}
