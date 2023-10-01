package kek.team.kokline.factories

import kek.team.kokline.service.ChatProcessor

object ChatProcessorFactory {
    fun <T> createProcessor(
        supplier: () -> String,
        mapper: (String) -> T,
        consumer: suspend (T) -> Unit
    ): ChatProcessor<T> = ChatProcessor(supplier, mapper, consumer)
}
