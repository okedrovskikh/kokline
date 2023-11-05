package kek.team.kokline.exceptions

import kek.team.kokline.models.CriticalLevels

sealed class CriticalException(message: String? = null, cause: Throwable? = null) : KoklineException(message, cause) {
    val criticalLevel: CriticalLevels = CriticalLevels.CRITICAL
}

class NoSessionInContextException : CriticalException("No session in context")
