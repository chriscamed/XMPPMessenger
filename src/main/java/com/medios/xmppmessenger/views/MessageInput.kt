package com.medios.xmppmessenger.views

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medios.xmppmessenger.R
import com.medios.xmppmessenger.di.ChatServerConnection
import com.medios.xmppmessenger.model.Message
import com.medios.xmppmessenger.theme.LocalCustomColorsPalette
import com.medios.xmppmessenger.theme.XMPPMessengerTheme
import com.medios.xmppmessenger.viewmodel.MessengerViewModel
import com.medios.xmppmessenger.viewmodel.MessengerViewModelPreview

@Composable
fun MessageInput(viewModel: MessengerViewModel) {
    var text by remember { mutableStateOf("") }
    var isCurrentUser by remember { mutableStateOf(true) }
    BasicTextField(
        modifier = Modifier.height(50.dp),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        value = text,
        onValueChange = { text = it },
        textStyle = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
        decorationBox = { innerTextField ->
            Row(modifier = Modifier
                .fillMaxSize()
                .background(LocalCustomColorsPalette.current.MessageOutBox)) {
                Box(modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 8.dp)
                    .clip(shape = RoundedCornerShape(15.dp))
                    .wrapContentHeight()
                    .weight(1f)
                    .background(LocalCustomColorsPalette.current.MessageInput)
                    .border(
                        1.dp,
                        LocalCustomColorsPalette.current.MessageInputBorder,
                        RoundedCornerShape(15.dp)
                    )
                    .padding(all = 8.dp), contentAlignment = Alignment.CenterStart) {
                    innerTextField()
                    if (text.isEmpty()) {
                        Text(text = "Message", fontSize = 12.sp)
                    }
                }

                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_send),
                    contentDescription = stringResource(id = R.string.send_image_description),
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                        .clip(shape = CircleShape)
                        .size(width = 40.dp, height = 40.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            viewModel.sendMessage(
                                Message(
                                    text = text,
                                    isFromCurrentUser = isCurrentUser,
                                    to = "chriscamed"
                                )
                            )
                            text = ""
                            isCurrentUser = !isCurrentUser
                        }
                )
            }
        }
    )
}

@Preview
@Composable
fun MessageInputPreview() {
    XMPPMessengerTheme {
        Surface {
            MessageInput(viewModel = MessengerViewModelPreview(ChatServerConnection()))
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MessageInputPreviewDark() {
    XMPPMessengerTheme {
        Surface {
            MessageInput(viewModel = MessengerViewModelPreview(ChatServerConnection()))
        }
    }
}