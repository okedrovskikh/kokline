package kek.team.kokline.factories

import kek.team.kokline.redis.consumer.MessageConsumer

object MessageConsumerFactory {

    fun createConsumer(channel: String): MessageConsumer = MessageConsumer(channel)
}