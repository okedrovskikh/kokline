package kek.team.kokline.models

/**
 * If requested for 1 user or 1 resource, will return lists with 1 element.
 * Using as for creation as for getting
 */
data class PreferenceDescription(val action: String, val ownersId: List<Long>, val resourcesId: List<Long>)
