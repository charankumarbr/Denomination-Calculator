package `in`.phoenix.denomcalc.model

/**
 * Created by Charan on December 07, 2020
 */
data class LineItem (
    val denominationType: Int,
    val lineDenomination: Int,
    var lineQty: Int
)