package kek.team.kokline.factories

import kek.team.kokline.redis.consumer.MessageConsumer

object MessageConsumerFactory {

    /**
     * Store current coroutine context in created MessageConsumer
     */
    suspend fun createConsumer(channel: String): MessageConsumer = MessageConsumer(channel)
}