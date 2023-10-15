package kek.team.kokline.support.utils

import org.jetbrains.exposed.sql.SizedCollection

fun <T> Collection<T>.toSizedCollection(): SizedCollection<T> = SizedCollection(this)
