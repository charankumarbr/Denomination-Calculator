package `in`.phoenix.denomcalc.adapter

import `in`.phoenix.denomcalc.model.Denomination

/**
 * Created by Charan on December 12, 2020
 */
interface OnSavedDenoClickListener {

    fun onSavedDenoClick(denomination: Denomination, position: Int)
}