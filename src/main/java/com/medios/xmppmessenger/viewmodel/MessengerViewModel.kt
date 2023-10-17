package com.medios.xmppmessenger.viewmodel

import androidx.lifecycle.ViewModel
import com.medios.xmppmessenger.di.ChatServerConnection
import com.medios.xmppmessenger.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class MessengerViewModel(
    private val chatConnection: ChatServerConnection
): ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    open fun sendMessage(message: Message, to: String) {
        _messages.value = _messages.value + message
        chatConnection.sendMessage(message = message.text, to = to)
    }
}

internal class MessengerViewModelPreview(
    chatConnection: ChatServerConnection
) : MessengerViewModel(chatConnection)