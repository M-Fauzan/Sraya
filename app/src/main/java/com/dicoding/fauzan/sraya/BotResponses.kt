package com.dicoding.fauzan.sraya

object BotResponses {
    fun reply(message: String): String {
        val lowercaseMessage = message.lowercase()
        return when {
            lowercaseMessage.contains("dipukuli") || lowercaseMessage.contains("ditendang") -> {
                "Pelaku dapat dikenakan UU nomor 12 tahun 2022 Pasal 6"
            }
            lowercaseMessage.contains("mengintip") -> {
                "Pelaku dapat dikenakan UU nomor 12 tahun 2022 Pasal 5"
            }
            else -> {
                "Maaf, ini tidak berhubungan dengan topik"
            }
        }
    }
}