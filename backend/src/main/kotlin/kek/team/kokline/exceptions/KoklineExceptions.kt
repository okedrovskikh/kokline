package kek.team.kokline.exceptions

sealed class KoklineExceptions(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
