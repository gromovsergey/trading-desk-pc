package com.foros.runner

import java.nio.file.Path
import java.nio.file.Paths

public trait GlassfishFinder {

    fun matches():Boolean

    fun find():Path

}

public class EnvironmentGlassfishFinder(val variable:String = "AS_HOME") : GlassfishFinder {

    override fun matches(): Boolean = System.getenv(variable) != null

    override fun find(): Path = Paths.get(System.getenv(variable)!!)!!.resolve("glassfish")!!

}

public class SystemParameterGlassfishFinder(val parameter:String = "glassfish.home") : GlassfishFinder {

    override fun matches(): Boolean = System.getProperty(parameter) != null

    override fun find(): Path = Paths.get(System.getProperty(parameter)!!)!!.resolve("glassfish")!!

}

public class FixedPathGlassfishFinder(val path:String) : GlassfishFinder {

    private val realPath = Paths.get(path)!!

    override fun matches(): Boolean = realPath.toFile().exists()

    override fun find(): Path = realPath

}

public class CurrentDirectoryGlassfishFinder : GlassfishFinder {

    override fun find(): Path = Paths.get("")!!.toAbsolutePath().getParent()!!

    override fun matches(): Boolean {
        val current = Paths.get("")!!
        return current.getFileName().toString() == "bin" &&
           current.getParent()!!.getFileName().toString() == "glassfish"
    }

}

public class ComplexGlassfishFinder(vararg val finders:GlassfishFinder) : GlassfishFinder {

    override fun matches(): Boolean = finders.any { it.matches() }

    override fun find(): Path = finders.first { it.matches() }.find()

}