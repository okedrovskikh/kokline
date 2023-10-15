package kek.team.kokline.models

import kek.team.kokline.support.serialization.DoubleAsLongSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Preference(@Serializable(with = DoubleAsLongSerializer::class) val resourceId: Long? = null, val action: String)

fun basicPreference() = Preference(null, "basic")
