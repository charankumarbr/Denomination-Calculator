package `in`.phoenix.denomcalc.room

import `in`.phoenix.denomcalc.room.model.DenominationCacheEntity
import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by Charan on December 11, 2020
 */
@Database(entities = [DenominationCacheEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {

    abstract fun denominationDao(): DenominationDao

    companion object {
        const val DATABASE_NAME: String = "denomination_db"
    }
}