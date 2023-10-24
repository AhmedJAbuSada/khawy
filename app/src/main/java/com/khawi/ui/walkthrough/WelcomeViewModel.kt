package com.khawi.ui.walkthrough

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.walkthrough.WelcomeRepository
import com.khawi.model.Welcome
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val welcomeRepository: WelcomeRepository
) : ViewModel() {

    private val _successLiveData = MutableLiveData<MutableList<Welcome>>()
    val successLiveData: LiveData<MutableList<Welcome>> = _successLiveData


    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    init {
        viewModelScope.launch {
            welcomeRepository.getWelcomeFlow().collect {

                when (it) {
                    is BaseState.NetworkError -> {
                        _progressLiveData.postValue(false)
                    }

                    is BaseState.EmptyResult -> {
                        _progressLiveData.postValue(false)
                    }

                    is BaseState.ItemsLoaded -> {
                        it.items?.let { item ->
                            _progressLiveData.postValue(false)
                            _successLiveData.postValue(item)
                            
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }

    fun getWelcome() = viewModelScope.launch {
        _progressLiveData.postValue(true)
        welcomeRepository.getWelcome()
    }


}