package kek.team.kokline.service

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::MessageService)
    singleOf(::IncomingMessageProducer)
    singleOf(::IncomingMessageConsumer)
}
