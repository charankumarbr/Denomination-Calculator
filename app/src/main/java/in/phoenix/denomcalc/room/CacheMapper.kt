package `in`.phoenix.denomcalc.room

import `in`.phoenix.denomcalc.model.Denomination
import `in`.phoenix.denomcalc.room.model.DenominationCacheEntity
import `in`.phoenix.denomcalc.util.EntityMapper
import javax.inject.Inject

/**
 * Created by Charan on December 11, 2020
 */
class CacheMapper
@Inject
constructor(): EntityMapper<DenominationCacheEntity, Denomination> {

    override fun mapFromEntity(entity: DenominationCacheEntity): Denomination {
        return Denomination(entity.id, entity.description, entity.totalValueForShare)
    }

    override fun mapToEntity(uiDomainModel: Denomination): DenominationCacheEntity {
        return DenominationCacheEntity(uiDomainModel.description, uiDomainModel.totalValueForShare)
    }

    fun mapFromEntityList(entities: List<DenominationCacheEntity>): List<Denomination>{
        return entities.map { mapFromEntity(it) }
    }

}