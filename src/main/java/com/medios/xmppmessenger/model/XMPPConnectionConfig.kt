package com.medios.xmppmessenger.model

class XMPPConnectionConfig {
    var userName: String? = null
    var password: String? = null
    var domain: String? = null
    var host: String? = null
    var port: Int = 5222
    class Builder {
        private val config = XMPPConnectionConfig()
        fun host(host: String) = apply { config.host = host }
        fun userName(userName: String) = apply { config.userName = userName }
        fun password(password: String) = apply { config.password = password }
        fun domain(domain: String) = apply { config.domain = domain }
        fun port(port: Int) = apply { config.port = port }
        fun build() = config
    }
}