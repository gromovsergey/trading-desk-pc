package com.foros.runner.processor

import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.io.FileWriter

public class LogProcessor : DomainProcessor {

    override val name: String = "Logs processor"

    override fun process(domain: Path) {
        println("- Processing logs")

        val logsPath = domain.resolve("logs")!!

        println("\tLogs dir: ${logsPath.toAbsolutePath()}")

        val currentLog = logsPath.resolve("server.log")!!.toFile()

        val now = SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(Date())
        val newLog = domain.resolve("logs/server.log_${now}")!!.toFile()

        if(now.size > 0) {
            println("\tCopy current log \n\t\tfrom ${currentLog.getName()} \n\t\tto ${newLog.getName()}")

            FileWriter(newLog).use {
                currentLog.reader().copyTo(it)
            }

            println("\tClear current log ${currentLog.getName()}")

            currentLog.writeText("")
        } else {
            println("\tСurrent log ${currentLog.getName()} is empty, skip this process")
        }


        println()
    }

}