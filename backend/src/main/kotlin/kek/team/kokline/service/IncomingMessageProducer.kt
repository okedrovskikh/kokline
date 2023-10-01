package kek.team.kokline.service

import kek.team.kokline.factories.Queue

class IncomingMessageProducer {

    fun sendEvent(event: String) {
        Queue.add(event)
    }
}
