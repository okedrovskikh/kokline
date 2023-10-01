package kek.team.kokline.factories

import kek.team.kokline.service.ChatProcessor

object ChatProcessorFactory {
    fun createProcessor(supplier: () -> String, consumer: (String) -> Unit): ChatProcessor = ChatProcessor(supplier, consumer)
}
