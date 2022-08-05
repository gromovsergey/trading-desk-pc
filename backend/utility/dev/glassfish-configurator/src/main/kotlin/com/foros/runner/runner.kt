package com.foros.runner

public fun main(args:Array<String>) {
    try {
        val arguments = Arguments(args).parse()
        GlassfishConfigurator().configure(arguments)
    } catch(e: Exception) {
        println("Glassfish configuration failed: ${e.getMessage()}")
        e.printStackTrace()
        System.exit(1)
    }
}

