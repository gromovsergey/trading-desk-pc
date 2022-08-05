package com.foros.runner.domain

public data class DbConfiguration(val host: String,
                                  val service: String,
                                  val port: Int,
                                  val user: String,
                                  val password: String,
                                  val biUser: String,
                                  val biPassword: String)

public data class DomainConfiguration(val name:String, val oracle: DbConfiguration, val pg:DbConfiguration, val olap: OlapConfiguration)

public data class Configuration(val domain:Map<String, DomainConfiguration>) {

    public fun get(name:String):DomainConfiguration? = domain.get(name)

}


public data class OlapConfiguration(val host:String, val user: String, val password:String, val schema:String)