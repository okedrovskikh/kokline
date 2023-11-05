package kek.team.kokline.models

import kek.team.kokline.security.actions.Actions
import kek.team.kokline.security.actions.Actions.BASIC

sealed class BasePreference {
    abstract val resourceId: Long?
    abstract val action: String
}

data class Preference(override val resourceId: Long? = null, override val action: String) : BasePreference() {

    constructor(resourceId: Long?, action: Actions) : this(resourceId, action.actionName)
}

data object BasicPreference : BasePreference() {
    override val resourceId: Long? = null
    override val action: String = BASIC.actionName
}


@Deprecated("Use BasicPreference", ReplaceWith("BasicPreference"))
fun basicPreference() = BasicPreference
