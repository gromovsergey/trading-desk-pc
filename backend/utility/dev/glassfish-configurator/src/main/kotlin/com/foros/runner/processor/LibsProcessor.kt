package com.foros.runner.processor

import java.nio.file.Path
import java.nio.file.Paths

public class LibsProcessor : DomainProcessor {

    override val name: String = "Libs processor"

    override fun process(domain: Path) {
        println("- Processing libs")

        val ouiHome = System.getenv("OUI_HOME")

        if(ouiHome == null)
            throw IllegalArgumentException("Please define OUI_HOME environment variable")

        val home = Paths.get(ouiHome)

        println("\tForos ui home dir: ${home.toAbsolutePath()}")

        val uiLibPath = home.resolve("target/lib")

        println("\tCopy libraries")
        println("\t\tfrom foros ui libs dir: ${uiLibPath.toAbsolutePath()}")

        val libPath = domain.resolve("lib")!!

        println("\t\tto domian libs dir: ${libPath.toAbsolutePath()}")

        val file = uiLibPath.toFile()
        if (file.exists().not() || file.isDirectory().not()) {
            println("\t\t${uiLibPath} doesn't exists and will be created")
            if (file.mkdirs().not())
                throw IllegalArgumentException("${uiLibPath} can't be created.")
        }

        file.listFiles()!!.forEach { it.copyTo(libPath.resolve(it.name)!!.toFile()) }

        println()
    }

}
