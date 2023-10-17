package com.medios.xmppmessenger.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import java.lang.Exception
import java.util.logging.Level
import java.util.logging.Logger

class ChatServerConnection {

    private val scope = CoroutineScope(Dispatchers.IO)
    fun sendMessage(message: String, to: String) {
        scope.launch {
            val config = XMPPTCPConnectionConfiguration.builder()
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setConnectTimeout(30000)
                //.setUsernameAndPassword(username + "@" + service, password)
                //.setServiceName(service)
                //.setHost(host)
                .setCompressionEnabled(true)
                //.setPort(port)
                .enableDefaultDebugger()
                //.setSocketFactory(SSLSocketFactory.getDefault())
                .setUsernameAndPassword("test", "test")
                .setXmppDomain("localhost")
                .setHost("192.168.1.15")
                .build()

            val connection = XMPPTCPConnection(config)
            connection.replyTimeout = 10000
            ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection()
            connection.connect()
            connection.login()

            val smackMessage: org.jivesoftware.smack.packet.Message? = connection.stanzaFactory
                .buildMessageStanza()
                .to(to)
                .setBody(message)
                .build()

            connection.sendStanza(smackMessage)

            connection.disconnect()

            val logger = Logger.getGlobal()

            connection.addConnectionListener(object : ConnectionListener {

                override fun connectionClosedOnError(e: Exception) {
                    logger.log(Level.INFO, "Connection closed on error");
                    // TODO: handle the connection closed on error
                }

                override fun connectionClosed() {
                    logger.log(Level.INFO, "Connection closed");
                    // TODO: handle the connection closed
                }

                override fun authenticated(arg0: XMPPConnection, arg1: Boolean) {
                    logger.log(Level.INFO, "User authenticated");
                    // TODO: handle the authentication
                }

                override fun connected(arg0: XMPPConnection) {
                    logger.log(Level.INFO, "Connection established");
                    // TODO: handle the connection
                }
            })
        }
    }
}