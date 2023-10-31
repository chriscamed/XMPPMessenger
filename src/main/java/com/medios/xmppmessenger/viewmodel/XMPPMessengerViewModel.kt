package com.medios.xmppmessenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medios.xmppmessenger.connection.XMPPMessenger
import com.medios.xmppmessenger.model.XMPPContact
import com.medios.xmppmessenger.model.XMPPMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import kotlin.coroutines.resume

abstract class XMPPMessengerViewModel(
    private val messenger: XMPPMessenger?
) : ViewModel() /*IncomingChatMessageListener*/ {

    private val _contactList = MutableStateFlow<Map<XMPPContact, List<XMPPMessage>>>(emptyMap())
    val contactList = _contactList.asStateFlow()

    private val _selectedContact = MutableStateFlow<XMPPContact?>(null)
    val selectedContact = _selectedContact.asStateFlow()

    private val _finishedLoadingContactList = MutableStateFlow(false)
    val finishedLoadingContactList = _finishedLoadingContactList.asStateFlow()

    private val _userNameToAdd = MutableStateFlow("")
    val userNameToAdd = _userNameToAdd.asStateFlow()

    private val _nickNameToAdd = MutableStateFlow("")
    val nickNameToAdd = _nickNameToAdd.asStateFlow()

    init {
        setupListeners()
    }

    private fun setupListeners() {
        messenger?.incomingChatMessageListener =
            IncomingChatMessageListener { from, message, chat ->
                val contact = XMPPContact(userName = from?.localpart?.asUnescapedString() ?: "", nickName = "")
                var messages = _contactList.value[contact] ?: emptyList()
                val msg = XMPPMessage(isFromCurrentUser = false, text = message?.body ?: "")
                messages = messages + msg
                _contactList.value = mapOf(contact to messages)
            }
    }

    open fun sendMessage(message: XMPPMessage, to: XMPPContact) {
        var messages = _contactList.value[to] ?: emptyList()
        messages = messages + message
        _contactList.value = mapOf(to to messages)
        viewModelScope.launch {
            messenger?.sendMessage(message, to)
        }
    }

    open fun setSelectedContact(contact: XMPPContact) {
        _selectedContact.value = contact
    }

    fun setUserNameToAdd(userName: String) {
        _userNameToAdd.value = userName
    }

    fun setNickNameToAdd(nickName: String) {
        _nickNameToAdd.value = nickName
    }
    suspend fun addContact(userName: String, nickName: String): Result<Unit> {
        _userNameToAdd.value = ""
        _nickNameToAdd.value = ""
        if (messenger != null) {
            if (userName.isNotEmpty()) {
                val result = messenger.addContact(userName, nickName).await()
                if (result.isSuccess) {
                    loadContactList()
                }
                return result
                /*runBlocking {
                    val job = launch {
                        val result = it.addContact(userName, nickName)
                        continuation.resume(result)
                    }
                    job.join()
                    loadContactList()
                }*/
            } else {
                return Result.failure(Exception("Username must not be null"))
            }
        } else {
            return Result.failure(Exception("Messenger object is null"))
        }
    }

    fun loadContactList() {
        _finishedLoadingContactList.value = false
        viewModelScope.launch {
            messenger?.getContactList()?.let {
                val contactsMap = mutableMapOf<XMPPContact, List<XMPPMessage>>()
                it.forEach { contact ->
                    contactsMap[contact] = emptyList()
                }
                _contactList.value = contactsMap
                _finishedLoadingContactList.value = true
            }
        }
    }

    fun loadMockContactList() {
        val contactsMap = mutableMapOf<XMPPContact, List<XMPPMessage>>()
        for (i in 1..<10) {
            val contact = XMPPContact("User name $i", "Nick name $i")
            contactsMap[contact] = emptyList()
        }
        _contactList.value = contactsMap
    }

    /*override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        val contact = XMPPContact(userName = from?.localpart?.asUnescapedString() ?: "", nickName = "")
        var messages = _contacts.value[contact] ?: emptyList()
        val msg = XMPPMessage(isFromCurrentUser = false, text = message?.body ?: "")
        messages = messages + msg
        _contacts.value = mapOf(contact to messages)
    }*/

}

internal class XMPPMessengerViewModelPreview : XMPPMessengerViewModel(null)