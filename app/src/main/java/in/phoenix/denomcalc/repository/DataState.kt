package `in`.phoenix.denomcalc.repository

/**
 * Created by Charan on December 11, 2020
 */
sealed class DataState<out R> {

    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
    object Loading : DataState<Nothing>()

}