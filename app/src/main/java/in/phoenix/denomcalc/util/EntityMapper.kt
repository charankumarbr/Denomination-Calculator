package `in`.phoenix.denomcalc.util

/**
 * Created by Charan on December 11, 2020
 */
interface EntityMapper<Entity, UIDomainModel> {

    fun mapFromEntity(entity: Entity): UIDomainModel

    fun mapToEntity(uiDomainModel: UIDomainModel): Entity

}