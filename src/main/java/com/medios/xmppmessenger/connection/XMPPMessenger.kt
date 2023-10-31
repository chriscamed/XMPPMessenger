package com.medios.xmppmessenger.connection

import android.content.Context
import com.medios.xmppmessenger.model.XMPPConnectionConfig
import com.medios.xmppmessenger.model.XMPPContact
import com.medios.xmppmessenger.model.XMPPMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.android.AndroidSmackInitializer
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.PresenceBuilder
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smackx.search.UserSearchManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jivesoftware.smackx.xdata.FormField
import org.jivesoftware.smackx.xdata.packet.DataForm
import org.jxmpp.jid.impl.JidCreate
import kotlin.coroutines.resume


class XMPPMessenger(config: XMPPConnectionConfig, context: Context) {

    private val scope by lazy { CoroutineScope(Dispatchers.IO) }
    private var connection: XMPPTCPConnection
    var incomingChatMessageListener: IncomingChatMessageListener? = null
        set(newValue) {
            ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection()
            val chatManager = ChatManager.getInstanceFor(connection)
            chatManager.addIncomingListener(newValue)
        }

    var connectionListener: XMPPMessengerConnectionListener? = null
        set(newValue) {
            setupConnectionListener(newValue)
        }

    init {
        AndroidSmackInitializer.initialize(context)
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
    }
    suspend fun addContact(userName: String, nickname: String = userName): Deferred<Result<Unit>> {
        return scope.async {
            if (connection.user.localpart.toString() == userName) {
                Result.failure(Exception("You can't add yourself as a contact"))
            } else {
                val result = userExists(connection, userName).await()
                if (result.isSuccess) {
                    val userExists = result.getOrDefault(false)
                    if (userExists) {
                        val contactToAddJid = "${userName}@${connection.xmppServiceDomain}"
                        val roster = Roster.getInstanceFor(connection)

                        if (!roster.isLoaded)
                            roster.reloadAndWait()

                        val contactBareJid = JidCreate.entityBareFrom(contactToAddJid)
                        if (roster.getEntry(contactBareJid) == null) {
                            roster.createItemAndRequestSubscription(contactBareJid, nickname, null)
                            Result.success(Unit)
                        } else {
                            Result.failure(Exception("Contact already added"))
                        }
                    } else {
                        Result.failure(Exception("User doesn't exist"))
                    }
                } else {
                    Result.failure(Exception(result.exceptionOrNull()?.message ?: ""))
                }
            }
        }
    }

    private fun addVCard(nickname: String, fullName: String, email: String): Deferred<Result<Unit>> {
        return scope.async {
            val vCardManager = VCardManager.getInstanceFor(connection)
            val vCard = VCard()
            vCard.nickName = nickname
            vCard.emailHome = email
            vCard.firstName = fullName
            try {
                vCardManager.saveVCard(vCard)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun userExists(connection: XMPPTCPConnection, username: String): Deferred<Result<Boolean>> {
        return scope.async {
            val userSearchManager = UserSearchManager(connection)
            val services = userSearchManager.searchServices
            if (services.isEmpty()) {
                Result.failure<Exception>(IllegalStateException("No search services available"))
            }

            val form = DataForm.builder(DataForm.Type.submit)

            val formTypeField = FormField.builder("FORM_TYPE")
                .setValue("jabber:iq:search")
                .build()

            val userField = FormField.textSingleBuilder("user").setValue(username).build()

            form.addField(formTypeField)
            form.addField(userField)

            val reportedData = userSearchManager.getSearchResults(
                form.build(),
                userSearchManager.searchServices.first()
            )

            Result.success(reportedData.rows.isNotEmpty())
        }
    }

    fun registerUser(userName: String, password: String, fullName: String, nickname: String, email: String): Deferred<Result<Unit>> {
        return scope.async {
            if (!connection.isConnected) {
                connection.connect()
            }
            val accountManager = AccountManager.getInstance(connection)
            accountManager.sensitiveOperationOverInsecureConnection(true)
            val entityFullJid = JidCreate.entityFullFrom("$userName@${connection.xmppServiceDomain}/resource1")
            val domainpart = entityFullJid.domain
            val localpart = entityFullJid.localpart
            return@async try {
                accountManager.createAccount(localpart, password)
                addVCard(nickname, fullName, email).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun connect(): Deferred<Result<Unit>> {
        return scope.async {
            if (!connection.isConnected) {
                try {
                    connection.connect()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            } else {
                Result.failure(Exception("Already connected"))
            }
        }
    }

    suspend fun connectAndLogin(username: String, password: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        if (!connection.isConnected) {
            scope.launch {
                try {
                    connection.connect()
                    connection.login(username, password)
                    continuation.resume(Result.success(Unit))
                } catch (e: Exception) {
                    continuation.resume(Result.failure(e))
                }
            }
        }

        continuation.invokeOnCancellation { shutDown() }
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

    fun shutDown() {
        connection.instantShutdown()
        connectionListener = null
    }

    fun sendMessage(message: XMPPMessage, to: XMPPContact) {
        scope.launch {
            val chatManager = ChatManager.getInstanceFor(connection)
            val recipientJid = "${to.userName}@${connection.xmppServiceDomain}"
            val chat = chatManager.chatWith(JidCreate.entityBareFrom(recipientJid))
            chat.send(message.text)
        }
    }

    private fun setupConnectionListener(listener: XMPPMessengerConnectionListener?) {
        connection.addConnectionListener(object : ConnectionListener {
            override fun connected(connection: XMPPConnection?) {
                super.connected(connection)
                listener?.connected(this@XMPPMessenger)
            }

            override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                super.authenticated(connection, resumed)
                listener?.authenticated(this@XMPPMessenger, resumed)
            }
        })
    }

    suspend fun getContactList(): List<XMPPContact> {
        val result = scope.async {
            val roster = Roster.getInstanceFor(connection)
            val entries: Collection<RosterEntry> = roster.entries
            val contacts = mutableListOf<XMPPContact>()
            for (entry in entries) {
                val jid = entry.jid
                val name = entry.name
                contacts.add(XMPPContact(jid.localpartOrNull.asUnescapedString(), name))
            }
            contacts
        }

        return result.await()
    }

    enum class ConnectionPriority(val value: Int) {
        LOW(1),
        MEDIUM(63),
        HIGH(127)
    }

}

interface XMPPMessengerConnectionListener {
    fun connected(connection: XMPPMessenger?)
    fun authenticated(connection: XMPPMessenger?, resumed: Boolean)
}