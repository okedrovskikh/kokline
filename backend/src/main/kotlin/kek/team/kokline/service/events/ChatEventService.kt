package kek.team.kokline.service.events

import io.github.oshai.kotlinlogging.KotlinLogging
import kek.team.kokline.models.Entity
import kek.team.kokline.models.EventResponse
import kek.team.kokline.models.Operation
import kek.team.kokline.redis.events.Events
import kek.team.kokline.support.utils.second

class ChatEventService : EventService {
    override val eventNames: Collection<Events> = Events.entries.filter { it.eventPrefix == "CHAT" }

    override suspend fun handleEvent(message: String, eventName: Events): EventResponse? {
        val ids = message.split(":")
        return when (eventName) {
            Events.CHAT_CREATE -> EventResponse(Entity.CHAT, ids.first().toLong(), Operation.CREATE)
            Events.CHAT_EDIT -> EventResponse(Entity.CHAT, ids.first().toLong(), Operation.UPDATE)
            Events.CHAT_MESSAGE_CREATE -> EventResponse(Entity.MESSAGE, ids.second().toLong(), Operation.CREATE)
            Events.CHAT_DELETE -> EventResponse(Entity.CHAT, ids.first().toLong(), Operation.DELETE)
            else -> {
                logger.warn { "Message with type = ${eventName.eventPrefix} in handler with type = CHAT" }
                null
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}