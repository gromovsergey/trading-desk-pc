package com.foros.runner

import java.util.HashMap

public class StringTemplate(val content:String) {

    private val values = HashMap<String, String>()

    public fun set(name:String, value:String) {
        values.put(name, value)
    }

    public fun generate():String {
        val builder = StringBuilder(content)

        for ((key, value) in values) {
            builder.replaceAll("{{${key}}}", value)
        }

        return builder.toString()
    }

}