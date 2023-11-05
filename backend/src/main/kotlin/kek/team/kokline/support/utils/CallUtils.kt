package kek.team.kokline.support.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.application.ApplicationCall
import io.ktor.websocket.Frame
import kek.team.kokline.exceptions.BadRequestException

/**
 * Don't use it. Made only for deserialization of frame data
 */
val objectMapper = jacksonObjectMapper()

fun <T> ApplicationCall.getParameter(name: String, mapper: (String) -> T): T {
    val parameter = parameters[name] ?: throw BadRequestException("No parameter named $name")
    return parameter.mapOrThrowBadRequest(mapper = mapper)
}

fun ApplicationCall.getId(): Long = getParameter("id") { it.toLong() }

fun ApplicationCall.getParameter(name: String): String = getParameter(name) { it }

inline fun <T, S> T.mapOrThrowBadRequest(errorMessage: String = "Invalid request: $this", crossinline mapper: (T) -> S): S = runCatching {
    mapper.invoke(this)
}.getOrElse {
    throw BadRequestException(errorMessage, it)
}

/**
 * Safe version of getId(). Should be used only in cases, when error during mapping should produce exception different from BadRequestException
 */
fun ApplicationCall.getIdOrNull(): Long? = parameters["id"]?.toLongOrNull()

inline fun <reified T> Frame.receiveDeserialized(): T = objectMapper.readValue<T>(data)
