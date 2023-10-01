package kek.team.kokline.service

import kek.team.kokline.factories.Queue

class IncomingMessageConsumer {

    fun receiveEvent(): String {
        var event: String? = null

        while (event == null) {
            event = Queue.nextMessage
        }

        return event
    }
}
