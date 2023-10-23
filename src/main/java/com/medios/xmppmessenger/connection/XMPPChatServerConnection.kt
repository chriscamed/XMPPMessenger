package com.medios.xmppmessenger.connection

import com.medios.xmppmessenger.model.XMPPMessage
import com.medios.xmppmessenger.model.XMPPConnectionConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.PresenceBuilder
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import java.lang.Exception

class XMPPChatServerConnection(config: XMPPConnectionConfig) {

    private val scope by lazy { CoroutineScope(Dispatchers.IO) }
    var connection: XMPPTCPConnection

    init {
        val connConfig = XMPPTCPConnectionConfiguration.builder()
            .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
            .setConnectTimeout(30000)
            .setSendPresence(true)
            .setCompressionEnabled(true)
            .setPort(config.port)
            .enableDefaultDebugger()
            //.setSocketFactory(SSLSocketFactory.getDefault())
            .setUsernameAndPassword(config.userName, config.password)
            .setXmppDomain(config.domain)
            .setHost(config.host)
            .build()

        connection = XMPPTCPConnection(connConfig)
        connection.replyTimeout = 10000
        ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection()

    }

    @Throws(Exception::class)
    fun connect() {
        if (!connection.isConnected) {
            connection.connect()
            connection.login()
        }
    }

    fun disconnect() {
        val presence = PresenceBuilder.buildPresence()
            .ofType(Presence.Type.unavailable)
            .setMode(Presence.Mode.away)
            .setStatus("Offline")
            .setPriority(ConnectionPriority.HIGH.value)
            .build()

        if (connection.isConnected) connection.disconnect(presence)
    }

    fun sendMessage(message: XMPPMessage) {
        scope.launch {
            val chatManager = ChatManager.getInstanceFor(connection)
            val recipientJid = "${message.to}@${connection.configuration.xmppServiceDomain}"
            val chat = chatManager.chatWith(JidCreate.entityBareFrom(recipientJid))
            chat.send(message.text)
        }
    }

    enum class ConnectionPriority(val value: Int) {
        LOW(1),
        MEDIUM(63),
        HIGH(127)
    }

}