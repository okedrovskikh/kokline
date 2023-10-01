package kek.team.kokline.service

class ChatProcessor(private val producer: () -> String, private val consumer: (String) -> Unit) {
    fun process() {
        consumer.invoke(producer.invoke())
    }
}
