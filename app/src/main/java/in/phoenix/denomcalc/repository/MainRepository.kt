package `in`.phoenix.denomcalc.repository

import `in`.phoenix.denomcalc.model.Denomination
import `in`.phoenix.denomcalc.model.ShareData
import `in`.phoenix.denomcalc.room.CacheMapper
import `in`.phoenix.denomcalc.room.DenominationDao
import `in`.phoenix.denomcalc.room.model.DenominationCacheEntity
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Charan on December 11, 2020
 */
@Module
@InstallIn(ApplicationComponent::class)
class MainRepository
@Inject
constructor(
    private val denominationDao: DenominationDao,
    private val cacheMapper: CacheMapper
){

    suspend fun getDenominations(): Flow<DataState<List<Denomination>>> = flow {
        emit(DataState.Loading)
        try {
            val denominations = denominationDao.getDenominations()
            emit(DataState.Success(cacheMapper.mapFromEntityList(denominations)))

        } catch (exception: Exception) {
            emit(DataState.Error(exception))
        }
    }

    suspend fun saveDenomination(shareData: ShareData): Flow<DataState<Long>> = flow {
        emit(DataState.Loading)
        try {
            val dbId = denominationDao.insert(DenominationCacheEntity(shareData.message, shareData.subject))
            emit(DataState.Success(dbId))
        } catch (exception: Exception) {
            emit(DataState.Error(exception))
        }
    }

}