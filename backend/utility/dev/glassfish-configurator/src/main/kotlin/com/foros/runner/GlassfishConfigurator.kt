package com.foros.runner

import com.foros.runner.processor.DomainProcessor
import com.foros.runner.processor.ConfigProcessor
import com.foros.runner.processor.LogProcessor
import com.foros.runner.processor.GeneratedResourcesProcessor
import com.foros.runner.processor.LibsProcessor
import java.nio.file.Path

public class GlassfishConfigurator {

    private val processorsStack = array(
            ConfigProcessor(),
            LogProcessor(),
            GeneratedResourcesProcessor(),
            LibsProcessor()
    )

    private val glassfishFinder = ComplexGlassfishFinder(
            SystemParameterGlassfishFinder(),
            EnvironmentGlassfishFinder(),
            CurrentDirectoryGlassfishFinder(),
            FixedPathGlassfishFinder("d:\\work\\glassfishv3\\glassfish\\")
    )

    public fun configure(arguments:Arguments):Boolean {
        println("Glassfish Foros Configuration start")

        if(!glassfishFinder.matches())
            throw IllegalStateException("Can't find glassfish! Please specify AS_HOME environment variable!")

        val glassfishPath = glassfishFinder.find()

        println("Glassfish directory: ${glassfishPath.toAbsolutePath()}")

        val action = Actions.find(arguments.arguments().get(0))

        println("Action: ${action}")

        var result = true

        if(action == Action.start) {
            val domain:String = arguments.arguments().get(1)

            val domainPath = glassfishPath.resolve("domains/${domain}/")!!

            println("Domain: ${domain}, dir: ${domainPath.toAbsolutePath()}\n")

            processorsStack.forEach { result = result && process(it, domainPath) }
        }

        if(result)
            println("Glassfish Foros configuration done.")
        else
            println("Glassfish Foros configuration failed. One or more processors failed, see log before.")

        return result
    }

    private fun process(processor:DomainProcessor, domain:Path):Boolean {
        try {
            processor.process(domain)
            return true
        } catch(e: Exception) {
            println("Processing ${processor.name} failed: ${e.getMessage()}")
            return false
        }
    }

}