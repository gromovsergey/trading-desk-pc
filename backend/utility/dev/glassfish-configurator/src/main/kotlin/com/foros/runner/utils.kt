package com.foros.runner

import java.util.regex.Pattern
import java.io.File

public fun String.find(pattern:String):Array<String>? {
    val matcher = Pattern.compile(pattern).matcher(this)

    if(matcher.find())
        return Array(matcher.groupCount()) { matcher.group(it+1)!! }
    else
        return null
}

public fun buildString(initial:String = "", block:StringBuilder.()->Unit):String {
    val builder = StringBuilder(initial)
    builder.block()
    return builder.toString()
}

public fun StringBuilder.replaceAll(from: String, to: String): StringBuilder {
    var index = this.indexOf(from)
    while (index != -1) {
        this.replace(index, index + from.length, to)
        index += to.length()
        index = this.indexOf(from, index)
    }
    return this
}