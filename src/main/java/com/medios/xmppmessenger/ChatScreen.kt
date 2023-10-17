package com.medios.xmppmessenger

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medios.xmppmessenger.views.MessageBubble
import com.medios.xmppmessenger.views.MessageInput
import com.medios.xmppmessenger.di.ChatServerConnection
import com.medios.xmppmessenger.model.Message
import com.medios.xmppmessenger.theme.XMPPMessengerColorsPalette
import com.medios.xmppmessenger.theme.XMPPMessengerTheme
import com.medios.xmppmessenger.viewmodel.MessengerViewModel
import com.medios.xmppmessenger.viewmodel.MessengerViewModelPreview
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(viewModel: MessengerViewModel, colors: XMPPMessengerColorsPalette) {
    XMPPMessengerTheme.updatePalette(colors)
    XMPPMessengerTheme {
        Surface {
            val messages by viewModel.messages.collectAsState()
            val lazyColumnListState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            Column {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    state = lazyColumnListState) {
                    coroutineScope.launch {
                        if (messages.isNotEmpty()) {
                            lazyColumnListState.scrollToItem(messages.count() - 1)
                        }
                    }
                    items(messages) {message ->
                        MessageBubble(message)
                    }
                }
                MessageInput(viewModel = viewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    XMPPMessengerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val remote = Message(text = stringResource(id = R.string.test_message), isFromCurrentUser= false, from = "Remote user")
            val current = Message(text = stringResource(id = R.string.lorem_ipsum), isFromCurrentUser = true, to = "Local user")
            val vm = MessengerViewModelPreview(ChatServerConnection())
            vm.sendMessage(remote)
            vm.sendMessage(current)
            vm.sendMessage(current)
            vm.sendMessage(current)
            vm.sendMessage(current)
            vm.sendMessage(remote)
            ChatScreen(vm, XMPPMessengerTheme.defaultColors)
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ChatScreenPreviewDark() {
    XMPPMessengerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val remote = Message(text = stringResource(id = R.string.test_message), isFromCurrentUser= false, from = "Remote user")
            val current = Message(text = stringResource(id = R.string.lorem_ipsum), isFromCurrentUser = true, to = "Local user")
            val vm = MessengerViewModelPreview(ChatServerConnection())
            vm.sendMessage(remote)
            vm.sendMessage(current)
            vm.sendMessage(current)
            vm.sendMessage(current)
            vm.sendMessage(current)
            vm.sendMessage(remote)
            ChatScreen(vm, XMPPMessengerTheme.defaultColors)
        }
    }
}