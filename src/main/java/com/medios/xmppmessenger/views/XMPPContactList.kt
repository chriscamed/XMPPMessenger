package com.medios.xmppmessenger.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medios.xmppmessenger.model.XMPPContact
import com.medios.xmppmessenger.theme.XMPPMessengerColorsPalette
import com.medios.xmppmessenger.viewmodel.XMPPMessengerViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medios.xmppmessenger.theme.OnDarkCustomColorsPalette
import com.medios.xmppmessenger.theme.OnLightCustomColorsPalette
import com.medios.xmppmessenger.theme.XMPPMessengerTheme
import com.medios.xmppmessenger.viewmodel.XMPPMessengerViewModelPreview

@Composable
fun XMPPContactList(
    modifier: Modifier = Modifier,
    viewModel: XMPPMessengerViewModel,
    colors: XMPPMessengerColorsPalette,
    onContactSelected: (contact: XMPPContact) -> Unit
) {
    XMPPMessengerTheme.updatePalette(colors)
    XMPPMessengerTheme {
        Surface(modifier = modifier) {
            val contacts by viewModel.contactList.collectAsStateWithLifecycle()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contacts.keys.toList()) { contact ->
                    ContactRow(contact, viewModel, onContactSelected)
                }
            }
        }
    }
}

@Composable
private fun ContactRow(contact: XMPPContact, viewModel: XMPPMessengerViewModel, onContactSelected: (contact: XMPPContact) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                viewModel.setSelectedContact(contact)
                onContactSelected(contact)
            }
    ) {
        Text(text = contact.userName, style = MaterialTheme.typography.bodyMedium)
        Text(text = contact.nickName ?: "", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
@Preview
fun XMPPContactListPreview() {
    Surface {
        XMPPMessengerTheme {
            val viewModel = XMPPMessengerViewModelPreview()
            viewModel.loadMockContactList()
            XMPPContactList(viewModel = viewModel, colors = XMPPMessengerColorsPalette(
                OnLightCustomColorsPalette, OnDarkCustomColorsPalette), onContactSelected = { } )
        }
    }
}