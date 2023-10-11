package kek.team.kokline.service.login

import io.ktor.server.auth.UserPasswordCredential
import kek.team.kokline.exceptions.ForbiddenException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.persistence.repositories.UserRepository
import kek.team.kokline.session.UserSession

class LoginService(private val repository: UserRepository) {

    suspend fun login(credits: UserPasswordCredential): Long = dbQuery {
        repository.findByCredits(credits.name, (credits.password).encodeToByteArray())?.id?.value
    } ?: throw ForbiddenException("Illegal credits: $credits")

    suspend fun validate(session: UserSession): Boolean = repository.isExist(session.id)
}
