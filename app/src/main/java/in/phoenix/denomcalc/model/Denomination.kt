package `in`.phoenix.denomcalc.model

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Charan on December 11, 2020
 */
data class Denomination
constructor(
    val id: Int,
    val description: String,
    val totalValueForShare: String): Parcelable {

    companion object CREATOR : Parcelable.Creator<Denomination> {

        class DenominationDiffCallback constructor() : DiffUtil.ItemCallback<Denomination>() {

            override fun areItemsTheSame(oldItem: Denomination, newItem: Denomination): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Denomination, newItem: Denomination): Boolean {
                return oldItem == newItem
            }
        }

        override fun createFromParcel(parcel: Parcel): Denomination {
            return Denomination(parcel)
        }

        override fun newArray(size: Int): Array<Denomination?> {
            return arrayOfNulls(size)
        }

    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
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

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(description)
        parcel.writeString(totalValueForShare)
    }

    override fun describeContents(): Int {
        return 0
    }
}