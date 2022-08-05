package com.foros.runner

public object Actions {

    public fun find(command:String):Action =
            Action.values().find { it.command == command } ?: throw IllegalArgumentException()

}

public enum class Action(val command:String) {

    start:Action("start-domain")
    stop:Action("stop-domain")

}