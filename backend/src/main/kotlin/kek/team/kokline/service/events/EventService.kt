package kek.team.kokline.service.events

import kek.team.kokline.models.EventResponse
import kek.team.kokline.redis.events.Events

interface EventService {
    val eventNames: Collection<Events>

    suspend fun handleEvent(message: String, eventName: Events): EventResponse?
}