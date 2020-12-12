package `in`.phoenix.denomcalc.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Charan on December 11, 2020
 */
@Entity(tableName = "denominations")
class DenominationCacheEntity
constructor(
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "totalValueForShare") var totalValueForShare: String
)
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}