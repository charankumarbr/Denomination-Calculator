package `in`.phoenix.denomcalc.repository

import `in`.phoenix.denomcalc.model.Denomination
import `in`.phoenix.denomcalc.room.CacheMapper
import `in`.phoenix.denomcalc.room.DenominationDao
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Charan on December 12, 2020
 */
@Module
@InstallIn(ApplicationComponent::class)
class StoredRepository
@Inject
constructor(
    private val denominationDao: DenominationDao,
    private val cacheMapper: CacheMapper
) {

    suspend fun getDenominations(): Flow<DataState<List<Denomination>>> = flow {
        emit(DataState.Loading)
        try {
            val denominations = denominationDao.getDenominations()
            emit(DataState.Success(cacheMapper.mapFromEntityList(denominations)))

        } catch (exception: Exception) {
            emit(DataState.Error(exception))
        }
    }

}