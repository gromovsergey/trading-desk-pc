package com.foros.runner.domain

import kotlin.properties.Delegates
import java.util.HashMap

public class DbConfigurationBuilder {

    private var host:String by Delegates.notNull()
    private var service:String by Delegates.notNull()
    private var port:Int by Delegates.notNull()
    private var user:String by Delegates.notNull()
    private var password:String by Delegates.notNull()
    private var biUser:String by Delegates.notNull()
    private var biPassword:String by Delegates.notNull()

    public fun host(host:String, service:String, port:Int) {
        this.host = host
        this.service = service
        this.port = port
    }

    public fun credentials(user:String, password:String) {
        this.user = user
        this.password = password
    }

    public fun biCredentials(user: String, password: String) {
        this.biUser = user
        this.biPassword = password
    }

    public fun build(): DbConfiguration = DbConfiguration(host, service, port, user, password, biUser, biPassword)
}

public class DomainConfigurationBuilder(val name:String) {

    private var olap: OlapConfiguration by Delegates.notNull()
    private var oracle: DbConfiguration by Delegates.notNull()
    private var pg: DbConfiguration by Delegates.notNull()

    public fun oracle(block:DbConfigurationBuilder.()->Unit) {
        val builder = DbConfigurationBuilder()
        builder.block()
        this.oracle = builder.build()
    }

    public fun oracle(host:String, service:String, user:String, password:String = "adserver", port:Int = 1521, biUser: String = "bi", biPassword: String = "adserver") {
        oracle {
            host(host, service, port)
            credentials(user, password)
            biCredentials(biUser, biPassword)
        }
    }

    public fun pg(block:DbConfigurationBuilder.()->Unit) {
        val builder = DbConfigurationBuilder()
        builder.block()
        this.pg = builder.build()
    }

    public fun pg(host:String, service:String, user:String = "ui", password:String = "adserver", port:Int = 5432, biUser: String = "bi", biPassword: String = "adserver") {
        pg {
            host(host, service, port)
            credentials(user, password)
            biCredentials(biUser, biPassword)
        }
    }

    public fun olap(host:String = "https://bi.foros-rubytest.net/pentaho/plugin/saiku/api/",
                    user:String = "bi.oixui",
                    password:String = "nv9UPkUPUj+TM",
                    schema: String  = "ForosUi moscow-dev-ui-oix-dev-12") {

        val fullSchemaName = "[$schema].[$schema].[$schema]"
        this.olap = OlapConfiguration(host, user, password, fullSchemaName)
    }

    public fun build(): DomainConfiguration = DomainConfiguration(name, oracle, pg, olap)
}


public class ConfigurationBuilder() {

    private val domains = HashMap<String, DomainConfiguration>()

    public fun domain(name:String, block:DomainConfigurationBuilder.()->Unit) {
        val builder = DomainConfigurationBuilder(name)
        builder.block()
        domains.put(name, builder.build())
    }

    public fun build():Configuration = Configuration(domains)
}

public fun configuration(block:ConfigurationBuilder.()->Unit):Configuration {
    val builder = ConfigurationBuilder()
    builder.block()
    return builder.build()
}