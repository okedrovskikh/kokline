package kek.team.kokline.mappers

import kek.team.kokline.models.ErrorResponse

class ExceptionsMapper {

    fun mapExceptionToError(throwable: Throwable) = ErrorResponse(throwable.message)
}
