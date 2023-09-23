package kek.team.kokline.repositories

import kek.team.kokline.models.User

class UserRepository(private val storage: MutableList<User> = mutableListOf()) {
    fun createUser(user: User) = storage.add(user)

    fun findAll() = storage.toList()

    fun findUser(id: Long?) = storage.filter { it.id != null }.find { it.id == id }

    fun delete(id: Long?) = storage.removeIf { it.id == id }
}