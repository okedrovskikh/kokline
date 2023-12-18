package kek.team.kokline.service.events

import kek.team.kokline.models.EventResponse
import kek.team.kokline.redis.events.Events

class EventsServiceFacade(
    chatEventService: ChatEventService,
    userEventService: UserEventService,
    messageEventService: MessageEventService,
) {
    private val eventServices: List<EventService> = listOf(chatEventService, userEventService, messageEventService)

    suspend fun handle(message: String, eventChannel: Events): EventResponse? =
        (eventServices.find { eventChannel in it.eventNames } ?: error("Cannot find service to handle event = $eventChannel"))
            .handleEvent(message, eventChannel)
}
