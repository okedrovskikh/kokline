package kek.team.kokline.service.events

import io.github.oshai.kotlinlogging.KotlinLogging
import kek.team.kokline.models.Entity
import kek.team.kokline.models.EventResponse
import kek.team.kokline.models.Operation
import kek.team.kokline.redis.events.Events

class UserEventService : EventService {
    override val eventNames: Collection<Events> = Events.entries.filter { it.eventPrefix == "USER" }

    override suspend fun handleEvent(message: String, eventName: Events): EventResponse? {
        val ids = message.split(":")
        return when (eventName) {
            Events.USER_EDIT -> EventResponse(Entity.USER, ids.first().toLong(), Operation.UPDATE)
            Events.USER_DELETE -> EventResponse(Entity.USER, ids.first().toLong(), Operation.DELETE)
            else -> {
                logger.warn { "Message with type = ${eventName.eventPrefix} in handler with type = USER" }
                null
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}