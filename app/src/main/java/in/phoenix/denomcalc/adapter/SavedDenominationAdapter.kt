package `in`.phoenix.denomcalc.adapter

import `in`.phoenix.denomcalc.R
import `in`.phoenix.denomcalc.adapter.listener.OnSavedDenoClickListener
import `in`.phoenix.denomcalc.model.Denomination
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import javax.inject.Inject

/**
 * Created by Charan on December 12, 2020
 */
class SavedDenominationAdapter
@Inject
constructor() : ListAdapter<Denomination, SavedDenoViewHolder>(
    Denomination.CREATOR.DenominationDiffCallback()
) {

    private var onSavedDenoClickListener: OnSavedDenoClickListener? = null

    fun setOnSavedDenominationClickListener(onSavedDenoClickListener: OnSavedDenoClickListener) {
        this.onSavedDenoClickListener = onSavedDenoClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedDenoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SavedDenoViewHolder(inflater.inflate(R.layout.layout_saved_item, parent, false), onSavedDenoClickListener)
    }

    override fun onBindViewHolder(holder: SavedDenoViewHolder, position: Int) {
        holder.setData(getItem(position)!!)
    }

}

class SavedDenoViewHolder(
    private val view: View,
    private val onSavedDenoClickListener: OnSavedDenoClickListener?
): RecyclerView.ViewHolder(view) {

    private val tvDesc: TextView = view.findViewById(R.id.lsiTvSaved)
    private var item: Denomination? = null

    fun setData(denomination: Denomination) {
        this.item = denomination
        tvDesc.text = denomination.description
        itemView.setOnClickListener {
            onSavedDenoClickListener?.onSavedDenoClick(denomination, adapterPosition)
        }
    }
}