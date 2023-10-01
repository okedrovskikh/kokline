package kek.team.kokline.service

class ChatProcessor<T>(
    private val producer: () -> String,
    private val mapper: (String) -> T,
    private val consumer: suspend (T) -> Unit
) {

    suspend fun process() = consumer.invoke(mapper.invoke(producer.invoke()))
}
