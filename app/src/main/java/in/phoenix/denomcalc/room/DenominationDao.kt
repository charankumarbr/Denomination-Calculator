package `in`.phoenix.denomcalc.room

import `in`.phoenix.denomcalc.room.model.DenominationCacheEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Charan on December 11, 2020
 */
@Dao
interface DenominationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogEntity: DenominationCacheEntity): Long

    @Query("SELECT * FROM denominations")
    suspend fun getDenominations(): List<DenominationCacheEntity>
}