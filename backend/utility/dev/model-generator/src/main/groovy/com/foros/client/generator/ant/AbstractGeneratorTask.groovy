package com.foros.client.generator.ant

import com.foros.client.generator.Generator
import com.foros.client.generator.schema.Schema
import com.foros.client.generator.schema.SchemaAggregator
import org.apache.tools.ant.Task

abstract class AbstractGeneratorTask extends Task {

    private Schema schema

    @Override
    void init() {
        schema = new SchemaAggregator().readSchema()
    }

    @Override
    void execute() {
        createGenerator(schema).generate()
    }

    protected abstract Generator createGenerator(Schema schema);

}
