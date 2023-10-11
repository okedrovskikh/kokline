package kek.team.kokline.support.utils

import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.CoroutineExceptionHandler

fun loggingCoroutineExceptionHandler(logger: KLogger) = CoroutineExceptionHandler { _, throwable -> logger.error(throwable) {} }
