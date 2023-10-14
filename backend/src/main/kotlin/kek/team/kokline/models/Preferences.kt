package kek.team.kokline.models

import kotlinx.serialization.Serializable

@Serializable
data class Preference(val resourceId: Long? = null, val action: String)

fun basicPreference() = Preference(null, "basic")
