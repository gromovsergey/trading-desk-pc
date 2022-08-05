package com.foros.runner.processor

import java.nio.file.Path
import com.foros.runner.StringTemplate
import com.foros.runner.domain.DomainConfiguration

public class ConfigProcessor : DomainProcessor {

    override val name: String = "Configuration processor"

    override fun process(domain: Path) {
        println("- Processing domain.xml")

        val db:String = System.getenv("FOROSDB")!!

        println("\tCofigure for db: ${db}")

        val configuration = Domains.config.get(db)

        if(configuration == null)
            throw IllegalArgumentException("Db configuration ${db} not found.")

        println("\t\tOracle: ${configuration.oracle.host}/${configuration.oracle.service}/${configuration.oracle.user}")
        println("\t\tPostgres: ${configuration.pg.host}/${configuration.pg.service}")

        val configPath = domain.resolve("config")!!

        println("\tConfig dir: ${configPath.toAbsolutePath()}")

        val templateFile = configPath.resolve("domain.template.xml")!!

        println("\tUsing template file: ${templateFile.toAbsolutePath()}")

        val template = StringTemplate(templateFile.toFile().readText())

        setTemplatesValues(template, configuration)

        val domainXmlFile = configPath.resolve("domain.xml")!!

        println("\tWriting result to: ${domainXmlFile.toAbsolutePath()}")

        domainXmlFile.toFile().writeText(template.generate())

        println()
    }

    private fun setTemplatesValues(template: StringTemplate, configuration: DomainConfiguration) {
        template["olap.host"] = configuration.olap.host
        template["olap.user"] = configuration.olap.user
        template["olap.password"] = configuration.olap.password
        template["olap.schema"] = configuration.olap.schema

        template["db.ora.url"] = "jdbc:oracle:thin:@//${configuration.oracle.host}:${configuration.oracle.port}/${configuration.oracle.service}"
        template["db.ora.user"] = configuration.oracle.user
        template["db.ora.password"] = configuration.oracle.password

        template["db.pg.serverName"] = configuration.pg.host
        template["db.pg.databaseName"] = configuration.pg.service
        template["db.pg.port"] = configuration.pg.port.toString()
        template["db.pg.user"] = configuration.pg.user
        template["db.pg.password"] = configuration.pg.password
        template["db.pg.biUser"] = configuration.pg.biUser
        template["db.pg.biPassword"] = configuration.pg.biPassword
    }
}
