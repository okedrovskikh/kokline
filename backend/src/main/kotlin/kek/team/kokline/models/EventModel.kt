package kek.team.kokline.models

import kek.team.kokline.support.utils.second

@JvmInline
value class EventModel(private val rawMessage: String) {
    val eventChannel: String get() = rawMessage.split("|", limit = 2).first()
    val message: String get() = rawMessage.split("|", limit = 2).second()
}
