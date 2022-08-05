package com.foros.runner.processor

import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Date
import com.foros.runner.StringTemplate

public trait DomainProcessor {

    val name:String

    fun process(domain:Path)

}


