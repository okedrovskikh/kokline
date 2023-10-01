package kek.team.kokline.factories

import kek.team.kokline.service.ChatProcessor

object ChatProcessorFactory {
    fun createProcessor(supplier: () -> String, consumer: suspend (String) -> Unit): ChatProcessor = ChatProcessor(supplier, consumer)
}
