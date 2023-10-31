package com.medios.xmppmessenger.views

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
import com.medios.xmppmessenger.R
import com.medios.xmppmessenger.model.XMPPContact
import com.medios.xmppmessenger.model.XMPPMessage
import com.medios.xmppmessenger.theme.XMPPMessengerColorsPalette
import com.medios.xmppmessenger.theme.XMPPMessengerTheme
import com.medios.xmppmessenger.viewmodel.XMPPMessengerViewModel
import com.medios.xmppmessenger.viewmodel.XMPPMessengerViewModelPreview
import kotlinx.coroutines.launch

@Composable
fun XMPPChatScreen(
    modifier: Modifier = Modifier,
    viewModel: XMPPMessengerViewModel,
    colors: XMPPMessengerColorsPalette
) {
    XMPPMessengerTheme.updatePalette(colors)
    XMPPMessengerTheme {
        Surface(modifier = modifier) {
            val currentContact by viewModel.selectedContact.collectAsState()
            val messages = viewModel.contactList.collectAsState().value[currentContact]
            val lazyColumnListState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            messages?.let {
                Column {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        state = lazyColumnListState) {
                        coroutineScope.launch {
                            if (it.isNotEmpty()) {
                                lazyColumnListState.scrollToItem(it.count() - 1)
                            }
                        }
                        items(it) {message ->
                            MessageBubble(message)
                        }
                    }
                    MessageInput(viewModel = viewModel)
                }
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
            val contact = XMPPContact("username", "nickname")
            val remote = XMPPMessage(text = stringResource(id = R.string.test_message), isFromCurrentUser= false)
            val current = XMPPMessage(text = stringResource(id = R.string.lorem_ipsum), isFromCurrentUser = true)
            val vm = XMPPMessengerViewModelPreview()
            vm.sendMessage(remote, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(remote, to = contact)
            XMPPChatScreen(viewModel = vm, colors = XMPPMessengerTheme.defaultColors)
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
            val contact = XMPPContact("username", "nickname")
            val remote = XMPPMessage(text = stringResource(id = R.string.test_message), isFromCurrentUser= false)
            val current = XMPPMessage(text = stringResource(id = R.string.lorem_ipsum), isFromCurrentUser = true)
            val vm = XMPPMessengerViewModelPreview()
            vm.sendMessage(remote, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(current, to = contact)
            vm.sendMessage(remote, to = contact)
            XMPPChatScreen(viewModel = vm, colors = XMPPMessengerTheme.defaultColors)
        }
    }
}