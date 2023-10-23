package com.medios.xmppmessenger.viewmodel

import androidx.lifecycle.ViewModel
import com.medios.xmppmessenger.connection.XMPPChatServerConnection
import com.medios.xmppmessenger.model.XMPPMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jxmpp.jid.EntityBareJid

abstract class XMPPMessengerViewModel(
    private val chatConnection: XMPPChatServerConnection?
) : ViewModel() {

    init {
        chatConnection?.connection?.let {
            val chatManager = ChatManager.getInstanceFor(it)
            chatManager.addIncomingListener { from, message, chat ->
                handleIncomingMessage(from, message, chat)
            }
        }
    }

    private val _messages = MutableStateFlow<List<XMPPMessage>>(emptyList())
    val messages: StateFlow<List<XMPPMessage>> = _messages.asStateFlow()
    open fun sendMessage(XMPPMessage: XMPPMessage) {
        _messages.value = _messages.value + XMPPMessage
        XMPPMessage.to?.let {
            chatConnection?.sendMessage(XMPPMessage)
        }
    }

    private fun handleIncomingMessage(from: EntityBareJid, message: org.jivesoftware.smack.packet.Message, chat: Chat) {
        val msg = XMPPMessage(from = from.intern(), isFromCurrentUser = false, text = message.body)
        _messages.value = _messages.value + msg
    }

}

internal class XMPPMessengerViewModelPreview : XMPPMessengerViewModel(null)