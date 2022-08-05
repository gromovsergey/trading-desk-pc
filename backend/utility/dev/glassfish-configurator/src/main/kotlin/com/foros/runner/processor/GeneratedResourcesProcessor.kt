package com.foros.runner.processor

import java.nio.file.Path

public class GeneratedResourcesProcessor : DomainProcessor {

    override val name: String = "Generated resources processor"

    override fun process(domain: Path) {
        println("- Processing generated resources")

        val directory = domain.resolve("generated")!!.toFile()

        if(!directory.exists()) {
            println("\tGenerated resources dir ${directory.getAbsolutePath()} not found, skip processing")
            return
        }

        println("\tGenerated resources dir: ${directory.getAbsolutePath()}")

        if(!directory.deleteRecursively())
            throw IllegalStateException("Can't delete generated resources.")

        println("\tAll generated files deleted")
        println()
    }

}