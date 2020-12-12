package `in`.phoenix.denomcalc.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize

/**
 * Created by Charan on December 11, 2020
 */
@Parcelize
data class Denomination
constructor(
    val id: Int,
    val description: String,
    val totalValueForShare: String): Parcelable {

    companion object {

        class DenominationDiffCallback constructor() : DiffUtil.ItemCallback<Denomination>() {

            override fun areItemsTheSame(oldItem: Denomination, newItem: Denomination): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Denomination, newItem: Denomination): Boolean {
                return oldItem == newItem
            }
        }

    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Denomination -> {
                this.id == other.id && this.description == other.description
            }

            else -> {
                false
            }
        }
    }
}