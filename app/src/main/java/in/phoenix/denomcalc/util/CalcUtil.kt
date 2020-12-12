package `in`.phoenix.denomcalc.util

import `in`.phoenix.denomcalc.model.DenominationConstants
import `in`.phoenix.denomcalc.model.LineItem
import `in`.phoenix.denomcalc.model.ShareData
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Charan on December 07, 2020
 */
object CalcUtil {

    fun dateFormat() = SimpleDateFormat("dd-MM-YYYY")

    fun getDateInFormat() = dateFormat().format(Date())

    fun formatQty(stringNumber: String?): Int {
        return if (stringNumber == null || TextUtils.isEmpty(stringNumber)) {
            0
        } else {
            try {
                stringNumber.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        }
    }

    fun allLineTotal(lineItems: MutableList<LineItem>): Int {
        var total = 0
        lineItems?.forEach {
            total += it.lineDenomination * it.lineQty
        }
        return total
    }

    fun prepareShareText(lineItems: MutableList<LineItem>): ShareData {
        val stringBuilder = StringBuilder()
        var maxQty = 1
        var maxLineTotal = 1
        var totalValue = 0
        lineItems.forEach {
            if (it.lineQty > maxQty) {
                maxQty = it.lineQty
            }

            val currentLineTotal = it.lineDenomination * it.lineQty
            if (currentLineTotal > maxLineTotal) {
                maxLineTotal = currentLineTotal
            }
            totalValue += currentLineTotal
        }

        val maxQtyLength = maxQty.toString().length
        val maxDenoLength = 4
        val maxLineTotalLength = maxLineTotal.toString().length

        var lastDisplayed = 0

        stringBuilder.append("--- Denomination ---")
        stringBuilder.append("\nDate: ${getDateInFormat()}")
        stringBuilder.append("\nNOTES:")
        val lineItemSize = lineItems.size
        for (lineItemIndex in 0 until lineItemSize) {
            val lineItem = lineItems[lineItemIndex]
            val denoQty = lineItem.lineQty

            if (denoQty > 0 ) {
                stringBuilder.append("\n")
                if (lineItemIndex > 0) {
                    if (lastDisplayed == DenominationConstants.TYPE_NOTE &&
                        lineItem.denominationType == DenominationConstants.TYPE_COIN) {
                        stringBuilder.append("COINS:")
                        stringBuilder.append("\n")
                    }
                }

                lastDisplayed = lineItem.denominationType
                val requiredDenoSpaces = maxDenoLength - lineItem.lineDenomination.toString().length
                if (requiredDenoSpaces > 0) {
                    for (index in 0..requiredDenoSpaces) {
                        stringBuilder.append(" ")
                    }
                }
                stringBuilder.append(lineItem.lineDenomination)

                stringBuilder.append(" X ")

                val requiredQtySpaces = maxQtyLength - denoQty.toString().length
                if (requiredQtySpaces > 0) {
                    for (index in 0 until requiredQtySpaces) {
                        stringBuilder.append(" ")
                    }
                }
                stringBuilder.append(denoQty)

                stringBuilder.append(" = ")

                val currentLineTotal = lineItem.lineDenomination * lineItem.lineQty
                val requiredAmountSpaces = maxLineTotalLength - currentLineTotal.toString().length
                if (requiredAmountSpaces > 0) {
                    for (index in 0..requiredAmountSpaces) {
                        stringBuilder.append(" ")
                    }
                }
                stringBuilder.append(currentLineTotal)
            }
        }

        stringBuilder.append("\n----------------------\n")
        stringBuilder.append("Total: $totalValue/-")
        stringBuilder.append("\n~~ App by: Phoenix Apps ~~")
        stringBuilder.append("\n----------------------\n")

        return ShareData("Denomination For Total: $totalValue", stringBuilder.toString())
    }


}