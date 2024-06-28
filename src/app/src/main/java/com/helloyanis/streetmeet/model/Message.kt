package com.helloyanis.streetmeet.model

import java.time.LocalDateTime

data class Message(var emitter: String, var dateTime: LocalDateTime, var content: String)
