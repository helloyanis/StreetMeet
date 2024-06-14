package com.helloyanis.streetmeet

import java.time.LocalDateTime

data class Message(var emitter: String, var dateTime: LocalDateTime, var content: String)
