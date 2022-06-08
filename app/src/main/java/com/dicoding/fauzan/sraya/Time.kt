package com.dicoding.fauzan.sraya

import java.text.SimpleDateFormat
import java.util.*

object Time {
    fun getTimeFormat(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
    }
}