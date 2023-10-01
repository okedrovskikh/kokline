package kek.team.kokline.service

class ChatProcessor(private val producer: () -> String, private val consumer: suspend (String) -> Unit) {
    suspend fun process() {
        consumer.invoke(producer.invoke())
    }
}
