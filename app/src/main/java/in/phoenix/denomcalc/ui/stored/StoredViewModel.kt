package `in`.phoenix.denomcalc.ui.stored

import `in`.phoenix.denomcalc.model.Denomination
import `in`.phoenix.denomcalc.repository.DataState
import `in`.phoenix.denomcalc.repository.StoredRepository
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Created by Charan on December 12, 2020
 */
@ExperimentalCoroutinesApi
class StoredViewModel
@ViewModelInject
constructor(
    private val storedRepository: StoredRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {


    private val _getSavedDenominationCount: MutableLiveData<DataState<List<Denomination>>> = MutableLiveData()
    val getSavedDenomination: LiveData<DataState<List<Denomination>>> = _getSavedDenominationCount

    fun getSavedDenomination() {
        viewModelScope.launch {
            storedRepository.getDenominations()
                .onEach { _dataState ->
                    _getSavedDenominationCount.postValue(_dataState)
                }.launchIn(viewModelScope)
        }
    }

}