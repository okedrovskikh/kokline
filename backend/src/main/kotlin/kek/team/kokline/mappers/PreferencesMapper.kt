package kek.team.kokline.mappers

import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.persistence.entities.PreferenceEntity

class PreferencesMapper {

    fun mapToDescription(featureName: String, entities: List<PreferenceEntity>): PreferenceDescription = PreferenceDescription(
        action = featureName,
        ownersId = entities.map { it.owner.id.value }.distinct(),
        resourcesId = entities.map { it.resourceId }.distinct()
    )
}
