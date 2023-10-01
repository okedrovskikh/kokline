package kek.team.kokline.mappers

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mapperModule = module {
    singleOf(::ChatMapper)
    singleOf(::MessageMapper)
    singleOf(::UserMapper)
}
