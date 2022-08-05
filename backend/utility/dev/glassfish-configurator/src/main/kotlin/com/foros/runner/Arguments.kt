package com.foros.runner

import java.util.HashMap
import java.util.ArrayList

public class Arguments(val args:Array<String>) {

    private val namedArguments = HashMap<String, String>()
    private val arguments = ArrayList<String>()

    public fun parse():Arguments {
        for (arg in args) {
            val parts = arg.find("\\-\\-(.*?)\\=(.*)")
            if(parts != null)
                namedArguments.put(parts[0], parts[1])
            else {
                val parts2 = arg.find("\\-\\-(.*)")
                if(parts2 != null)
                    namedArguments.put(parts2[0], "true")
                else
                    arguments.add(arg)
            }
        }

        return this
    }

    public fun arguments():List<String> = arguments
    public fun namedArguments():Map<String, String> = namedArguments

}