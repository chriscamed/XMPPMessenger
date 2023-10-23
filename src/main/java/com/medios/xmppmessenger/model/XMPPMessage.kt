package com.medios.xmppmessenger.model

import java.util.UUID

data class XMPPMessage(
    val id: UUID = UUID.randomUUID(),
    val text: String,
    val from: String? = null,
    val to: String? = null,
    val isFromCurrentUser: Boolean
)

