package com.medios.xmppmessenger.views

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.medios.xmppmessenger.model.XMPPMessage
import com.medios.xmppmessenger.theme.LocalCustomColorsPalette
import com.medios.xmppmessenger.theme.XMPPMessengerTheme

@Composable
internal fun MessageBubble(message: XMPPMessage) {
    Box(modifier = Modifier
        .padding(
            top = 8.dp,
            bottom = 8.dp,
            start = if (message.isFromCurrentUser) 64.dp else 16.dp,
            end = if (!message.isFromCurrentUser) 64.dp else 16.dp
        )
        .fillMaxWidth(), contentAlignment = if (message.isFromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart) {
        ElevatedCard(elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )) {
            ConstraintLayout(modifier = Modifier
                .wrapContentWidth()
                .background(if (message.isFromCurrentUser) LocalCustomColorsPalette.current.MessageBubbleCurrentUser else LocalCustomColorsPalette.current.MessageBubbleRemoteUser)
                .padding(8.dp)) {
                val (hourText, messageText) = createRefs()
                Text(modifier = Modifier.constrainAs(messageText) {
                    top.linkTo(parent.top)
                }, text = message.text, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                Text(modifier = Modifier
                    .constrainAs(hourText) {
                        top.linkTo(messageText.bottom, margin = 4.dp)
                        end.linkTo(parent.end)
                    }, text = "5:45pm", textAlign = TextAlign.End, fontSize = 12.sp, fontStyle = FontStyle.Italic, color = LocalCustomColorsPalette.current.MessageHour
                )
            }
        }
    }
}

@Preview
@Composable
fun MessageBubblePreview() {
    XMPPMessengerTheme {
        Surface {
            MessageBubble(message = XMPPMessage(text = "Test message", isFromCurrentUser = true))
        }
    }
}