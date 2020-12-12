package `in`.phoenix.denomcalc.ui.home

import `in`.phoenix.denomcalc.model.Denomination
import `in`.phoenix.denomcalc.model.ShareData
import `in`.phoenix.denomcalc.repository.DataState
import `in`.phoenix.denomcalc.repository.MainRepository
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Created by Charan on December 11, 2020
 */
@ExperimentalCoroutinesApi
class MainViewModel
@ViewModelInject
constructor(
    private val mainRepository: MainRepository,
    @Assisted private val stateHandle: SavedStateHandle
): ViewModel() {

    private val _saveDenomination: MutableLiveData<DataState<Long>> = MutableLiveData()
    val saveDenomination: LiveData<DataState<Long>> = _saveDenomination

    fun saveDenomination(shareData: ShareData) {
        viewModelScope.launch {
            mainRepository.saveDenomination(shareData)
                .onEach { _dataState ->
                    _saveDenomination.value = _dataState
                }.launchIn(viewModelScope)
        }
    }

    private val _getSavedDenominationCount: MutableLiveData<DataState<List<Denomination>>> = MutableLiveData()
    val getSavedDenomination: LiveData<DataState<List<Denomination>>> = _getSavedDenominationCount

    fun getSavedDenominationCount() {
        viewModelScope.launch {
            mainRepository.getDenominations()
                .onEach { _dataState ->
                    _getSavedDenominationCount.postValue(_dataState)
                }.launchIn(viewModelScope)
        }
    }
}